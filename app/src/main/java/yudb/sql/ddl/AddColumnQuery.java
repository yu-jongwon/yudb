package yudb.sql.ddl;

import yudb.engine.Transaction;
import yudb.scan.TableScan;
import yudb.sql.Query;

public class AddColumnQuery extends Query {

  private final String tableName;
  private final String columnName;

  public AddColumnQuery(String tableName, String columnName) {
    this.tableName = tableName;
    this.columnName = columnName;
  }

  public boolean execute(Transaction tx) {
    var columns = TableScan.getColumnInfoScan(tx);
    while (columns.next()) {
      if (columns.getObject("table_name").equals(tableName) &&
          columns.getObject("column_name").equals(columnName)) {
        return false;
      }
    }

    columns.insert();
    columns.setObject("table_name", tableName);
    columns.setObject("column_name", columnName);
    columns.setObject("type_name", "JAVA_OBJECT");
    columns.setObject("data_type", java.sql.Types.JAVA_OBJECT);

    tx.addColumn(tableName);
    return true;
  }

}
