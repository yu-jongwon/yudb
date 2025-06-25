package yudb.sql.ddl;

import java.util.List;

import yudb.engine.Transaction;
import yudb.scan.TableScan;
import yudb.sql.Query;

public class CreateTableQuery extends Query {

  private final String tableName;
  private final List<String> columnNames;

  public CreateTableQuery(String tableName, List<String> columnNames) {
    this.tableName = tableName;
    this.columnNames = columnNames;
  }

  public boolean execute(Transaction tx) {
    var tables = TableScan.getTableInfoScan(tx);
    while (tables.next()) {
      if (tables.getObject("table_name").equals(tableName)) {
        return false;
      }
    }

    tables.beforeFirst();
    tables.insert();
    tables.setObject("table_name", tableName);
    tables.setObject("table_type", "TABLE");

    var columns = TableScan.getColumnInfoScan(tx);
    for (var columnName: columnNames) {
      columns.insert();
      columns.setObject("table_name", tableName);
      columns.setObject("column_name", columnName);
      columns.setObject("type_name", "JAVA_OBJECT");
      columns.setObject("data_type", java.sql.Types.JAVA_OBJECT);
    }

    tx.createTable(tableName);
    return true;
  }

}
