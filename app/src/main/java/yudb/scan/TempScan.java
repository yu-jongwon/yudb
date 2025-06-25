package yudb.scan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class TempScan extends Scan {

  private final List<String> columnLabels;
  private final List<List<Object>> rows = new ArrayList<>();
  private int rowIndex = -1;

  public TempScan(List<String> columnLabels) {
    this.columnLabels = columnLabels;
    this.beforeFirst();
  }

  @Override
  public void beforeFirst() {
    rowIndex = -1;
  }

  @Override
  public boolean hasColumnLabel(String columnLabel) {
    return indexOf(columnLabels, columnLabel) != -1;
  }

  @Override
  public List<String> getColumnLabels() {
    return columnLabels;
  }

  @Override
  public Object getObject(String columnLabel) {
    var columnIndex = indexOf(columnLabels, columnLabel);
    if (columnIndex == -1) {
      return null;
    }
    return rows.get(rowIndex).get(columnIndex);
  }

  @Override
  public void setObject(String columnLabel, Object value) {
    var columnIndex = indexOf(columnLabels, columnLabel);
    if (columnIndex == -1) {
      return;
    }
    rows.get(rowIndex).set(columnIndex, value);
  }

  @Override
  public int insert() {
    var columnCount = columnLabels.size();
    var newRow = new ArrayList<Object>(Collections.nCopies(columnCount, null));
    rows.add(newRow);
    return rowIndex = rows.size() - 1;
  }

  @Override
  public boolean next() {
    while (rowIndex < rows.size() - 1) {
      rowIndex += 1;
      return true;
    }
    return false;
  }

}
