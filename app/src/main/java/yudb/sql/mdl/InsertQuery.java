package yudb.sql.mdl;

import java.util.List;

import yudb.engine.Transaction;
import yudb.sql.expr.Expr;
import yudb.scan.TableScan;
import yudb.sql.Query;

public class InsertQuery extends Query {

  private final String tableName;
  private final List<String> columnLabels;
  private final List<List<Expr>> valueList;

  public InsertQuery(String tableName, List<String> columnLabels, List<List<Expr>> valueList) {
    this.tableName = tableName;
    this.columnLabels = columnLabels;
    this.valueList = valueList;
  }

  public int execute(Transaction tx) {
    var scan = new TableScan(tx, tableName);
    for (var values : valueList) {
      scan.insert();
      for (int i = 0; i < columnLabels.size(); i++) {
        scan.setObject(columnLabels.get(i), values.get(i).evaluate(scan));
      }
    }
    return valueList.size();
  }

}
