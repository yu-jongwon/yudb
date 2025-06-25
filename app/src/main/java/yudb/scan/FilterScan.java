package yudb.scan;

import java.util.List;

import yudb.engine.Transaction;
import yudb.sql.expr.Expr;

public class FilterScan extends Scan {

  private final Scan scan;
  private final Expr expr;

  public FilterScan(Scan scan, Expr expr) {
    this.scan = scan;
    this.expr = expr;
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
    return scan.hasColumnLabel(columnLabel);
  }

  @Override
  public List<String> getColumnLabels() {
    return scan.getColumnLabels();
  }

  @Override
  public Object getObject(String columnLabel) {
    return scan.getObject(columnLabel);
  }

  @Override
  public void setObject(String columnLabel, Object value) {
    scan.setObject(columnLabel, value);
  }

  @Override
  public void delete() {
    scan.delete();
  }

  @Override
  public boolean next() {
    while (scan.next()) {
      if (Boolean.TRUE.equals(expr.evaluate(scan))) {
        return true;
      }
    }
    return false;
  }

}
