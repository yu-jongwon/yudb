package yudb.sql.ddl;

import yudb.engine.Transaction;
import yudb.scan.TableScan;
import yudb.sql.Query;

public class DropColumnQuery extends Query {

  private final String tableName;
  private final String columnName;

  public DropColumnQuery(String tableName, String columnName) {
    this.tableName = tableName;
    this.columnName = columnName;
  }

  public boolean execute(Transaction tx) {
    var result = false;
    var columnIndex = -1;
    var columns = TableScan.getColumnInfoScan(tx);
    while (columns.next()) {
      if (columns.getObject("table_name").equals(tableName)) {
        columnIndex += 1;
        if (columns.getObject("column_name").equals(columnName)) {
          columns.delete();
          tx.dropColumn(tableName, columnIndex);
          result = true;
        }
      }
    }
    return result;
  }

}
