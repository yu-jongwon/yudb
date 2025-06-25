package yudb.sql.ddl;

import yudb.engine.Transaction;
import yudb.scan.TableScan;
import yudb.sql.Query;

public class DropTableQuery extends Query {

  private final String tableName;

  public DropTableQuery(String tableName) {
    this.tableName = tableName;
  }

  public boolean execute(Transaction tx) {
    var result = false;

    var tables = TableScan.getTableInfoScan(tx);
    while (tables.next()) {
      if (tables.getObject("table_name").equals(tableName)) {
        tables.delete();
        result = true;
      }
    }

    var columns = TableScan.getColumnInfoScan(tx);
    while (columns.next()) {
      if (columns.getObject("table_name").equals(tableName)) {
        columns.delete();
      }
    }

    tx.dropTable(tableName);
    return result;
  }

}
