package yudb.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Storage {

  private class Row extends ArrayList<Object> {
    boolean isDeleted = true;

    Row(int columnCount) {
      for (var i = 0; i < columnCount; i++) {
        add(null);
      }
    }

    Row(Object... objects) {
      isDeleted = false;
      for (Object obj : objects) {
          add(obj);
      }
    }
  }

  private class TableInfoScheme extends Row {
    TableInfoScheme(String tableName) {
      super(tableName, "TABLE");
    }
  }

  private class ColumnInfoScheme extends Row {
    ColumnInfoScheme(String tableName, String columnName) {
      super(tableName, columnName, "JAVA_OBJECT", java.sql.Types.JAVA_OBJECT);
    }
  }

  private final static Storage instance = new Storage();
  private final Map<String, List<Row>> tables = new HashMap<>() {{
    put("@tables", new ArrayList<>() {{
      add(new TableInfoScheme("@tables"));
      add(new TableInfoScheme("@columns"));
    }});

    put("@columns", new ArrayList<>() {{
      add(new ColumnInfoScheme("@tables", "table_name"));
      add(new ColumnInfoScheme("@tables", "table_type"));

      add(new ColumnInfoScheme("@columns", "table_name"));
      add(new ColumnInfoScheme("@columns", "column_name"));
      add(new ColumnInfoScheme("@columns", "type_name"));
      add(new ColumnInfoScheme("@columns", "data_type"));
    }});
  }};

  static Storage getInstance() {
    return instance;
  }

  void createTable(String tableName) {
    tables.put(tableName, new ArrayList<>());
  }

  void renameTable(String oldTableName, String newTableName) {
    tables.put(newTableName, tables.get(oldTableName));
  }

  void dropTable(String tableName) {
    tables.remove(tableName);
  }

  void addColumn(String tableName) {
    for (var row : tables.get(tableName)) {
      row.add(null);
    }
  }

  void dropColumn(String tableName, int columnIndex) {
    for (var row : tables.get(tableName)) {
      row.remove(columnIndex);
    }
  }

  int size(String tableName) {
    if (!tables.containsKey(tableName)) {
      return 0;
    }
    return tables.get(tableName).size();
  }

  Object getObject(String tableName, int rowIndex, int columnIndex) {
    return tables.get(tableName).get(rowIndex).get(columnIndex);
  }

  void setObject(String tableName, int rowIndex, int columnIndex, Object value) {
    tables.get(tableName).get(rowIndex).set(columnIndex, value);
  }

  int insert(String tableName, int columnCount) {
    tables.get(tableName).add(new Row(columnCount));
    return tables.get(tableName).size() - 1;
  }

  void setDelete(String tableName, int rowIndex, boolean delete) {
    tables.get(tableName).get(rowIndex).isDeleted = delete;
  }

  boolean isDeleted(String tableName, int rowIndex) {
    return tables.get(tableName).get(rowIndex).isDeleted;
  }

}
