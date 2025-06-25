package yudb.sql.mdl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import yudb.engine.Transaction;
import yudb.sql.expr.AggrExpr;
import yudb.sql.expr.Expr;
import yudb.scan.CrossJoinScan;
import yudb.scan.FilterScan;
import yudb.scan.FullJoinScan;
import yudb.scan.GroupScan;
import yudb.scan.InnerJoinScan;
import yudb.scan.LeftJoinScan;
import yudb.scan.LimitScan;
import yudb.scan.OffsetScan;
import yudb.scan.ProjectScan;
import yudb.scan.AliasScan;
import yudb.scan.RightJoinScan;
import yudb.scan.SelectScan;
import yudb.scan.SingleRowScan;
import yudb.scan.Scan;
import yudb.scan.SortScan;
import yudb.scan.TableScan;
import yudb.sql.Query;

public class SelectQuery extends Query {

  List<Expr> selects;
  TableSource from;
  List<Join> joins;
  Expr where;
  List<Expr> groups;
  List<AggrExpr> aggrs;
  Expr having;
  List<Order> orders;
  int offset = -1;
  int limit = -1;

  public SelectQuery(
    List<Expr> selects,
    TableSource from,
    List<Join> joins,
    Expr where,
    List<Expr> groups,
    List<AggrExpr> aggrs,
    Expr having,
    List<Order> orders,
    int offset,
    int limit
  ) {
    this.selects = Objects.requireNonNullElse(selects, new ArrayList<>());
    this.from = from;
    this.joins = Objects.requireNonNullElse(joins, new ArrayList<>());
    this.where = where;
    this.groups = Objects.requireNonNullElse(groups, new ArrayList<>());
    this.aggrs = Objects.requireNonNullElse(aggrs, new ArrayList<>());
    this.having = having;
    this.orders = Objects.requireNonNullElse(orders, new ArrayList<>());
    this.offset = offset;
    this.limit = limit;
  }

  public Scan createScan(Transaction tx) {
    Scan scan = new SingleRowScan();
    if (from != null) scan = from.createScan(tx);
    for (var join: joins) {
      var right = join.tableSource.createScan(tx);
      scan = switch (join.joinType) {
        case Cross -> new CrossJoinScan(scan, right);
        case Inner -> new InnerJoinScan(scan, right, join.on);
        case Left -> new LeftJoinScan(scan, right, join.on);
        case Right -> new RightJoinScan(scan, right, join.on);
        case Full -> new FullJoinScan(scan, right, join.on);
      };
    }
    if (where != null) scan = new FilterScan(scan, where);
    if (aggrs.isEmpty() == false) {
      scan = new GroupScan(scan, aggrs, groups);
      if (having != null) scan = new FilterScan(scan, having);
    }
    scan = new SelectScan(scan, selects);
    if (orders.isEmpty() == false) scan = new SortScan(scan, orders);
    if (offset >= 0) scan = new OffsetScan(scan, offset);
    if (limit >= 0) scan = new LimitScan(scan, limit);
    return new ProjectScan(scan, selects);
  }

  public static interface TableSource {
    public Scan createScan(Transaction tx);
  }

  public static class NamedTable implements TableSource {
    String tableName;
    String tableAlias;

    public NamedTable(String tableName, String tableAlias) {
      this.tableName = tableName;
      this.tableAlias = tableAlias;
    }

    @Override
    public Scan createScan(Transaction tx) {
      var scan = new TableScan(tx, tableName);
      return new AliasScan(scan, tableAlias);
    }
  }

  public static class SubQueryTable implements TableSource {
    SelectQuery query;
    String tableAlias;

    public SubQueryTable(SelectQuery query, String tableAlias) {
      this.query = query;
      this.tableAlias = tableAlias;
    }

    @Override
    public Scan createScan(Transaction tx) {
      var scan = query.createScan(tx);
      return new AliasScan(scan, tableAlias);
    }
  }

  public static class Join {
    JoinType joinType;
    TableSource tableSource;
    Expr on;

    public Join(JoinType joinType, TableSource tableSource, Expr on) {
      this.joinType = joinType;
      this.tableSource = tableSource;
      this.on = on;
    }
  }

  public static enum JoinType {
    Cross, Inner, Left, Right, Full
  }

  public static class Order {
    public Expr expr;
    public Direction direction;

    public Order(Expr expr) {
      this.expr = expr;
      this.direction = Direction.Asc;
    }

    public Order(Expr expr, Direction direction) {
      this.expr = expr;
      this.direction = direction;
    }
  }

  public static enum Direction {
    Asc, Desc
  }

}
