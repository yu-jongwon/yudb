package yudb.sql.ddl;

import yudb.engine.Transaction;
import yudb.scan.TableScan;
import yudb.sql.Query;

public class RenameTableQuery extends Query {

  private final String oldTableName;
  private final String newTableName;

  public RenameTableQuery(String oldTableName, String newTableName) {
    this.oldTableName = oldTableName;
    this.newTableName = newTableName;
  }

  public boolean execute(Transaction tx) {
    var result = false;

    var tables = TableScan.getTableInfoScan(tx);
    while (tables.next()) {
      if (tables.getObject("table_name").equals((newTableName))) {
        return false;
      }

      if (tables.getObject("table_name").equals(oldTableName)) {
        tables.setObject("table_name", newTableName);
        result = true;
      }
    }

    var columns = TableScan.getColumnInfoScan(tx);
    while (columns.next()) {
      if (columns.getObject("table_name").equals(oldTableName)) {
        columns.setObject("table_name", newTableName);
      }
    }

    tx.renameTable(oldTableName, newTableName);
    return result;
  }

}
