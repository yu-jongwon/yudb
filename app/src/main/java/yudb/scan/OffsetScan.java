package yudb.scan;

import java.util.List;

import yudb.engine.Transaction;

public class OffsetScan extends Scan {

  private final Scan scan;
  private final int offset;

  public OffsetScan(Scan scan, int offset) {
    this.scan = scan;
    this.offset = offset;
    this.beforeFirst();
  }

  @Override
  public Transaction getTransaction() {
    return scan.getTransaction();
  }

  @Override
  public void beforeFirst() {
    scan.beforeFirst();
    for (var i = 0; i < offset && scan.next(); i++);
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
    return scan.next();
  }

}
