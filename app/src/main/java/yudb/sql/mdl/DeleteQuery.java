package yudb.sql.mdl;

import yudb.engine.Transaction;
import yudb.sql.expr.Expr;
import yudb.scan.FilterScan;
import yudb.scan.Scan;
import yudb.scan.TableScan;
import yudb.sql.Query;

public class DeleteQuery extends Query {

  private final String tableName;
  private final Expr pred;

  public DeleteQuery(String tableName) {
    this.tableName = tableName;
    this.pred = null;
  }

  public DeleteQuery(String tableName, Expr pred) {
    this.tableName = tableName;
    this.pred = pred;
  }

  public int execute(Transaction tx) {
    Scan scan = new TableScan(tx, tableName);
    if (pred != null) {
      scan = new FilterScan(scan, pred);
    }

    var count = 0;
    while (scan.next()) {
      scan.delete();
      count++;
    }
    return count;
  }

}
