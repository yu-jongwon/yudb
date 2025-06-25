package yudb.scan;

import java.util.ArrayList;
import java.util.List;

import yudb.engine.Transaction;

public class TableScan extends Scan {

  private static final String TABLE_INFO_TABLE_NAME = "@tables";
  private static final String COLUMN_INFO_TABLE_NAME = "@columns";
  private static final List<String> TABLE_INFO_COLUMN_LABELS = List.of("table_name", "table_type");
  private static final List<String> COLUMN_INFO_COLUMN_LABELS = List.of("table_name", "column_name", "type_name", "data_type");

  private final Transaction tx;
  private final String tableName;
  private final List<String> columnLabels;
  private int rowIndex = -1;

  public TableScan(Transaction tx, String tableName) {
    this.tx = tx;
    this.tableName = tableName;
    this.columnLabels = (switch (tableName) {
      case TABLE_INFO_TABLE_NAME -> TABLE_INFO_COLUMN_LABELS;
      case COLUMN_INFO_TABLE_NAME -> COLUMN_INFO_COLUMN_LABELS;
      default -> getColumnLabels(tx, tableName);
    }).stream().map(columnLabel -> tableName + "." + columnLabel).toList();
    this.beforeFirst();
  }

  @Override
  public Transaction getTransaction() {
    return tx;
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
    return tx.getObject(tableName, rowIndex, columnIndex);
  }

  @Override
  public void setObject(String columnLabel, Object value) {
    var columnIndex = indexOf(columnLabels, columnLabel);
    if (columnIndex == -1) {
      return;
    }
    tx.setObject(tableName, rowIndex, columnIndex, value);
  }

  @Override
  public int insert() {
    while (rowIndex < tx.size(tableName) - 1) {
      rowIndex += 1;
      if (tx.isDeleted(tableName, rowIndex) == true) {
        tx.setDelete(tableName, rowIndex, false);
        return rowIndex;
      }
    }

    rowIndex = tx.insert(tableName, columnLabels.size());
    tx.setDelete(tableName, rowIndex, false);
    return rowIndex;
  }

  @Override
  public void delete() {
    tx.setDelete(tableName, rowIndex, true);
  }

  @Override
  public boolean next() {
    while (rowIndex < tx.size(tableName) - 1) {
      rowIndex += 1;
      if (tx.isDeleted(tableName, rowIndex) == false) {
        return true;
      }
    }
    return false;
  }

  public static Scan getTableInfoScan(Transaction tx) {
    return new TableScan(tx, TABLE_INFO_TABLE_NAME);
  }

  public static Scan getColumnInfoScan(Transaction tx) {
    return new TableScan(tx, COLUMN_INFO_TABLE_NAME);
  }

  private static List<String> getColumnLabels(Transaction tx, String tableName) {
    var columnLabels = new ArrayList<String>();
    var scan = new TableScan(tx, COLUMN_INFO_TABLE_NAME);
    while (scan.next()) {
      if (tableName.equals(scan.getObject("table_name"))) {
        var columnLabel = (String) scan.getObject("column_name");
        columnLabels.add(columnLabel);
      }
    }
    return columnLabels;
  }

}
