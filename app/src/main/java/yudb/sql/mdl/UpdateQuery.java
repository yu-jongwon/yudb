package yudb.sql.mdl;

import java.util.Map;

import yudb.engine.Transaction;
import yudb.sql.expr.Expr;
import yudb.scan.FilterScan;
import yudb.scan.Scan;
import yudb.scan.TableScan;
import yudb.sql.Query;

public class UpdateQuery extends Query {

  private final String tableName;
  private final Map<String, Expr> values;
  private final Expr pred;

  public UpdateQuery(String tableName, Map<String, Expr> values) {
    this.tableName = tableName;
    this.values = values;
    this.pred = null;
  }

  public UpdateQuery(String tableName, Map<String, Expr> values, Expr pred) {
    this.tableName = tableName;
    this.values = values;
    this.pred = pred;
  }

  public int execute(Transaction tx) {
    Scan scan = new TableScan(tx, tableName);
    if (pred != null) {
      scan = new FilterScan(scan, pred);
    }

    var count = 0;
    while (scan.next()) {
      for (var entry : values.entrySet()) {
        scan.setObject(entry.getKey(), entry.getValue().evaluate(scan));
      }
      count++;
    }
    return count;
  }

}
