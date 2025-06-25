package yudb.scan;

import java.util.List;
import java.util.stream.Stream;

import yudb.engine.Transaction;

public class CrossJoinScan extends Scan {

  private final Scan left;
  private final Scan right;

  public CrossJoinScan(Scan left, Scan right) {
    this.left = left;
    this.right = right;
    this.beforeFirst();
  }

  @Override
  public Transaction getTransaction() {
    return left.getTransaction();
  }

  @Override
  public void beforeFirst() {
    left.beforeFirst();
    right.beforeFirst();
    left.next();
  }

  @Override
  public List<String> getColumnLabels() {
    return Stream.concat(left.getColumnLabels().stream(), right.getColumnLabels().stream()).toList();
  }

  @Override
  public boolean hasColumnLabel(String columnLabel) {
    return left.hasColumnLabel(columnLabel) || right.hasColumnLabel(columnLabel);
  }

  @Override
  public Object getObject(String columnLabel) {
    if (left.hasColumnLabel(columnLabel)) {
      return left.getObject(columnLabel);
    }
    return right.getObject(columnLabel);
  }

  @Override
  public boolean next() {
    if (right.next()) {
      return true;
    }
    right.beforeFirst();
    return left.next() && right.next();
  }

}
