package yudb.scan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import yudb.engine.Transaction;
import yudb.sql.expr.Expr;

public class SelectScan extends Scan {

  private final Scan scan;
  private final Map<String, Expr> exprs = new HashMap<>();

  public SelectScan(Scan scan, List<Expr> selects) {
    this.scan = scan;
    for (var expr: selects) {
      var columnLabel = expr.toString().toLowerCase();
      if (columnLabel.endsWith("*") == false) {
        this.exprs.put(columnLabel, expr);
      }
    }
    this.beforeFirst();
  }

  @Override
  public Transaction getTransaction() {
    return scan.getTransaction();
  }

  @Override
  public void beforeFirst() {
    scan.beforeFirst();
  }

  @Override
  public boolean hasColumnLabel(String columnLabel) {
    return scan.hasColumnLabel(columnLabel) || exprs.containsKey(columnLabel);
  }

  @Override
  public List<String> getColumnLabels() {
    return Stream.concat(scan.getColumnLabels().stream(), exprs.keySet().stream()).toList();
  }

  @Override
  public boolean next() {
    return scan.next();
  }

  @Override
  public Object getObject(String columnLabel) {
    if (exprs.containsKey(columnLabel)) {
      return exprs.get(columnLabel).evaluate(scan);
    }
    return scan.getObject(columnLabel);
  }

}
