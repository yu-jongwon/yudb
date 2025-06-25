package yudb.scan;

import java.util.List;

import yudb.engine.Transaction;

public class SingleRowScan extends Scan {

  private int rowIndex = -1;

  public Transaction getTransaction() {
    return null;
  }

  @Override
  public void beforeFirst() {
    rowIndex = -1;
  }

  @Override
  public boolean hasColumnLabel(String columnLabel) {
    return false;
  }

  @Override
  public List<String> getColumnLabels() {
    return List.of();
  }

  @Override
  public Object getObject(String columnLabel) {
    return null;
  }

  @Override
  public boolean next() {
    if (rowIndex == -1) {
      rowIndex = 0;
      return true;
    }
    return false;
  }

}
