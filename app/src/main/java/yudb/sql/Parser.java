package yudb.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import yudb.sql.expr.AggrExpr;
import yudb.sql.expr.AsExpr;
import yudb.sql.expr.BinaryExpr;
import yudb.sql.expr.ConstExpr;
import yudb.sql.expr.Expr;
import yudb.sql.expr.FieldExpr;
import yudb.sql.expr.InExpr;
import yudb.sql.expr.LikeExpr;
import yudb.sql.expr.ListExpr;
import yudb.sql.expr.QueryExpr;
import yudb.sql.ddl.AddColumnQuery;
import yudb.sql.ddl.CreateTableQuery;
import yudb.sql.ddl.DropColumnQuery;
import yudb.sql.ddl.DropTableQuery;
import yudb.sql.ddl.RenameColumnQuery;
import yudb.sql.ddl.RenameTableQuery;
import yudb.sql.mdl.DeleteQuery;
import yudb.sql.mdl.InsertQuery;
import yudb.sql.mdl.SelectQuery;
import yudb.sql.mdl.UpdateQuery;
import yudb.sql.mdl.SelectQuery.Direction;
import yudb.sql.mdl.SelectQuery.Join;
import yudb.sql.mdl.SelectQuery.JoinType;
import yudb.sql.mdl.SelectQuery.Order;
import yudb.sql.mdl.SelectQuery.TableSource;
import yudb.sql.mdl.SelectQuery.NamedTable;
import yudb.sql.mdl.SelectQuery.SubQueryTable;
import yudb.sql.tcl.CommitQuery;
import yudb.sql.tcl.RollbackQuery;

public class Parser {

  Tokenizer tokenizer;
  List<AggrExpr> aggrs = new ArrayList<>();

  public static Query parse(String sql) {
    var parser = new Parser(sql);
    return parser.parseQuery();
  }

  private Parser(String sql) {
    this.tokenizer = new Tokenizer(sql);
  }

  private Query parseQuery() {
    return switch (tokenizer.peek()) {
      case "commit"   -> parseCommitQuery();
      case "rollback" -> parseRollbackQuery();
      case "create"   -> parseCreateTableQuery();
      case "rename"   -> parseRenameTableQuery();
      case "drop"     -> parseDropTableQuery();
      case "alter"    -> parseAlterTableQuery();
      case "select"   -> parseSelectQuery();
      case "insert"   -> parseInsertQuery();
      case "update"   -> parseUpdateQuery();
      case "delete"   -> parseDeleteQuery();
      default -> throw new RuntimeException("Syntax error: " + tokenizer.peek());
    };
  }

  private CommitQuery parseCommitQuery() {
    tokenizer.pop("commit");
    return new CommitQuery();
  }

  private RollbackQuery parseRollbackQuery() {
    tokenizer.pop("rollback");
    return new RollbackQuery();
  }

  private CreateTableQuery parseCreateTableQuery() {
    tokenizer.pop("create");
    tokenizer.pop("table");
    var tableName = tokenizer.popIdent();

    var columnNames = new ArrayList<String>();
    tokenizer.pop("(");
    do {
      var columnName = tokenizer.popIdent();
      columnNames.add(columnName);
    } while (tokenizer.popIf(","));
    tokenizer.pop(")");

    return new CreateTableQuery(tableName, columnNames);
  }

  private RenameTableQuery parseRenameTableQuery() {
    tokenizer.pop("rename");
    tokenizer.pop("table");
    var tableName = tokenizer.popIdent();
    tokenizer.pop("to");
    var newTableName = tokenizer.popIdent();

    return new RenameTableQuery(tableName, newTableName);
  }

  private DropTableQuery parseDropTableQuery() {
    tokenizer.pop("drop");
    tokenizer.pop("table");
    var tableName = tokenizer.popIdent();

    return new DropTableQuery(tableName);
  }

  private Query parseAlterTableQuery() {
    tokenizer.pop("alter");
    tokenizer.pop("table");
    var tableName = tokenizer.popIdent();

    if (tokenizer.popIf("add")) {
      tokenizer.pop("column");
      var columnName = tokenizer.popIdent();
      return new AddColumnQuery(tableName, columnName);
    }

    if (tokenizer.popIf("rename")) {
      if (tokenizer.popIf("column")) {
        var oldColumnName = tokenizer.popIdent();
        tokenizer.pop("to");
        var newColumnName = tokenizer.popIdent();
        return new RenameColumnQuery(tableName, oldColumnName, newColumnName);
      }
      tokenizer.pop("to");
      var newTableName = tokenizer.popIdent();
      return new RenameTableQuery(tableName, newTableName);
    }

    if (tokenizer.popIf("drop")) {
      tokenizer.pop("column");
      var columnName = tokenizer.popIdent();
      return new DropColumnQuery(tableName, columnName);
    }

    throw new RuntimeException("Syntax error: " + tokenizer.peek());
  }

  private SelectQuery parseSelectQuery() {
    var selects = new ArrayList<Expr>();
    tokenizer.pop("select");
    do {
      var expr = parseExpr();
      tokenizer.popIf("as");
      if (tokenizer.peek().equals("ident")) {
        var alias = tokenizer.popIdent();
        expr = new AsExpr(alias, expr);
      }
      selects.add(expr);
    } while (tokenizer.popIf(","));

    TableSource from = null;
    if (tokenizer.popIf("from")) {
      from = parseTableSource();
    }

    var joins = new ArrayList<Join>();
    parseJoins: while (true) {
      switch (tokenizer.peek()) {
        case "inner" -> {
          tokenizer.pop("inner");
          continue parseJoins;
        }

        case "join" -> {
          tokenizer.pop("join");
          var joinTableSource = parseTableSource();
          tokenizer.pop("on");
          var onExpr = parseExpr();
          joins.add(new Join(JoinType.Inner, joinTableSource, onExpr));
        }

        case "cross" -> {
          tokenizer.pop("cross");
          tokenizer.pop("join");
          var joinTableSource = parseTableSource();
          joins.add(new Join(JoinType.Cross, joinTableSource, null));
        }

        case "left" -> {
          tokenizer.pop("left");
          tokenizer.popIf("outer");
          tokenizer.pop("join");
          var joinTableSource = parseTableSource();
          tokenizer.pop("on");
          var onExpr = parseExpr();
          joins.add(new Join(JoinType.Left, joinTableSource, onExpr));
        }

        case "right" -> {
          tokenizer.pop("right");
          tokenizer.popIf("outer");
          tokenizer.pop("join");
          var joinTableSource = parseTableSource();
          tokenizer.pop("on");
          var onExpr = parseExpr();
          joins.add(new Join(JoinType.Right, joinTableSource, onExpr));
        }

        case "full" -> {
          tokenizer.pop("full");
          tokenizer.popIf("outer");
          tokenizer.pop("join");
          var joinTableSource = parseTableSource();
          tokenizer.pop("on");
          var onExpr = parseExpr();
          joins.add(new Join(JoinType.Full, joinTableSource, onExpr));
        }

        default -> {
          break parseJoins;
        }
      }
    }

    Expr where = null;
    if (tokenizer.popIf("where")) {
      where = parseExpr();
    }

    var groups = new ArrayList<Expr>();
    if (tokenizer.popIf("group")) {
      tokenizer.pop("by");
      do {
        groups.add(parseExpr());
      } while (tokenizer.popIf(","));
    }

    Expr having = null;
    if (tokenizer.popIf("having")) {
      having = parseExpr();
    }

    var orders = new ArrayList<Order>();
    if (tokenizer.popIf("order")) {
      tokenizer.pop("by");
      do {
        var expr = parseExpr();
        if (tokenizer.popIf("desc")) {
          orders.add(new Order(expr, Direction.Desc));
        } else {
          tokenizer.popIf("asc");
          orders.add(new Order(expr, Direction.Asc));
        }
      } while (tokenizer.popIf(","));
    }

    int offset = -1;
    if (tokenizer.popIf("offset")) {
      offset = (int) tokenizer.pop("integer");
    }

    int limit = -1;
    if (tokenizer.popIf("limit")) {
      limit = (int) tokenizer.pop("integer");
    }

    return new SelectQuery(selects, from, joins, where, groups, aggrs, having, orders, offset, limit);
  }

  public TableSource parseTableSource() {
    if (tokenizer.popIf("(")) {
      var query = parseSelectQuery();
      tokenizer.pop(")");
      tokenizer.popIf("as");
      var tableAlias = tokenizer.popIdent();
      return new SubQueryTable(query, tableAlias);
    }

    var tableName = tokenizer.popIdent();
    var tableAlias = tableName;
    tokenizer.popIf("as");
    if (tokenizer.peek().equals("ident")) {
      tableAlias = tokenizer.popIdent();
    }
    return new NamedTable(tableName, tableAlias);
  }

  public InsertQuery parseInsertQuery() {
    tokenizer.pop("insert");
    tokenizer.pop("into");
    var tableName = tokenizer.popIdent();

    var columnLabels = new ArrayList<String>();
    tokenizer.pop("(");
    do {
      columnLabels.add(tokenizer.popIdent());
    } while (tokenizer.popIf(","));
    tokenizer.pop(")");

    var valueList = new ArrayList<List<Expr>>();
    tokenizer.pop("values");
    do {
      var values = new ArrayList<Expr>();
      tokenizer.pop("(");
      do {
        values.add(parseExpr());
      } while (tokenizer.popIf(","));
      tokenizer.pop(")");
      valueList.add(values);
    } while (tokenizer.popIf(","));

    return new InsertQuery(tableName, columnLabels, valueList);
  }

  public UpdateQuery parseUpdateQuery() {
    tokenizer.pop("update");
    var tableName = tokenizer.popIdent();

    var values = new HashMap<String, Expr>();
    tokenizer.pop("set");
    do {
      var columnLabel = tokenizer.popIdent();
      tokenizer.pop("=");
      var value = parseExpr();
      values.put(columnLabel, value);
    } while (tokenizer.popIf(","));

    if (tokenizer.popIf("where") == false) {
      return new UpdateQuery(tableName, values);
    }

    var pred = parseExpr();
    return new UpdateQuery(tableName, values, pred);
  }

  public DeleteQuery parseDeleteQuery() {
    tokenizer.pop("delete");
    tokenizer.pop("from");
    var tableName = tokenizer.popIdent();

    if (tokenizer.popIf("where") == false) {
      return new DeleteQuery(tableName);
    }

    var pred = parseExpr();
    return new DeleteQuery(tableName, pred);
  }

  private Expr parseExpr() {
    return parseOr();
  }

  private Expr parseOr() {
    var lhs = parseAnd();
    while (tokenizer.popIf("or")) {
      var rhs = parseAnd();
      lhs = new BinaryExpr("or", lhs, rhs);
    }
    return lhs;
  }

  private Expr parseAnd() {
    var lhs = parseRelational();
    while (tokenizer.popIf("and")) {
      var rhs = parseRelational();
      lhs = new BinaryExpr("and", lhs, rhs);
    }
    return lhs;
  }

  private Expr parseRelational() {
    var ops = Set.of("==", "!=", "<", "<=", ">", ">=", "in", "like");
    var lhs = parseArithmetic1();
    while (ops.contains(tokenizer.peek())) {
      var op = tokenizer.pop();
      var rhs = parseArithmetic1();
      lhs = switch (op) {
        case "in" -> new InExpr(lhs, rhs);
        case "like" -> new LikeExpr(lhs, rhs);
        default -> new BinaryExpr(op, lhs, rhs);
      };
    }
    return lhs;
  }

  private Expr parseArithmetic1() {
    var ops = Set.of("+", "-");
    var lhs = parseArithmetic2();
    while (ops.contains(tokenizer.peek())) {
      var op = tokenizer.pop();
      var rhs = parseArithmetic2();
      lhs = new BinaryExpr(op, lhs, rhs);
    }
    return lhs;
  }

  private Expr parseArithmetic2() {
    var ops = Set.of("*", "/", "%");
    var lhs = parseOperand();
    while (ops.contains(tokenizer.peek())) {
      var op = tokenizer.pop();
      var rhs = parseOperand();
      lhs = new BinaryExpr(op, lhs, rhs);
    }
    return lhs;
  }

  private Expr parseOperand() {
    switch (tokenizer.peek()) {
      case "*" -> {
        return new FieldExpr(tokenizer.pop());
      }
      case "ident" -> {
        var iden = tokenizer.popIdent();
        if (tokenizer.popIf(".")) {
          if (tokenizer.popIf("*")) {
            return new FieldExpr(iden + ".*");
          }
          var field = tokenizer.popIdent();
          return new FieldExpr(iden + "." + field);
        }
        return new FieldExpr(iden);
      }
      case "integer", "double", "string", "true", "false", "null" -> {
        return new ConstExpr(tokenizer.popLiteral());
      }
      case "(" -> {
        var values = new ArrayList<Expr>();
        tokenizer.pop("(");
        do {
          if (tokenizer.peek().equals("select")) {
            var query = parseSelectQuery();
            values.add(new QueryExpr(query));
          } else {
            values.add(parseExpr());
          }
        } while (tokenizer.popIf(","));
        tokenizer.pop(")");
        if (values.size() == 1) {
          return values.get(0);
        }
        return new ListExpr(values);
      }
      case "min", "max", "count", "sum", "avg" -> {
        var fn = tokenizer.pop();
        tokenizer.pop("(");
        var expr = parseExpr();
        tokenizer.pop(")");
        var aggr = new AggrExpr(fn, expr);
        aggrs.add(aggr);
        return aggr;
      }
    };
    throw new RuntimeException("invalid operand " + tokenizer.peek());
  }

}
