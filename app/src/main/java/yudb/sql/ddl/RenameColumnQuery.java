package yudb.sql.ddl;

import yudb.engine.Transaction;
import yudb.scan.TableScan;
import yudb.sql.Query;

public class RenameColumnQuery extends Query {

  private final String tableName;
  private final String oldColumnName;
  private final String newColumnName;

  public RenameColumnQuery(String tableName, String oldColumnName, String newColumnName) {
    this.tableName = tableName;
    this.oldColumnName = oldColumnName;
    this.newColumnName = newColumnName;
  }

  public boolean execute(Transaction tx) {
    var result = false;
    var columns = TableScan.getColumnInfoScan(tx);
    while (columns.next()) {
      if (columns.getObject("table_name").equals(tableName) &&
          columns.getObject("column_name").equals(newColumnName)) {
        return false;
      }

      if (columns.getObject("table_name").equals(tableName) &&
          columns.getObject("column_name").equals(oldColumnName)) {
        columns.setObject("column_name", newColumnName);
        result = true;
      }
    }
    return result;
  }

}
