package yudb.scan;

import java.util.List;

import yudb.engine.Transaction;

public class LimitScan extends Scan {

  private final Scan scan;
  private final int limit;
  private int count = 0;

  public LimitScan(Scan scan, int limit) {
    this.scan = scan;
    this.limit = limit;
    this.beforeFirst();
  }

  @Override
  public Transaction getTransaction() {
    return scan.getTransaction();
  }

  @Override
  public void beforeFirst() {
    scan.beforeFirst();
    count = 0;
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
  public boolean next() {
    if (count < limit) {
      count += 1;
      return scan.next();
    }
    return false;
  }

}
