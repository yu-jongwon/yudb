package yudb.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transaction {

  private enum LockType { S, X }

  private interface Log {}

  private record SetObjectLog (
    String tableName,
    Integer rowIndex,
    Integer columnIndex,
    Object value
  ) implements Log {}

  private record SetDeleteLog (
    String tableName,
    Integer rowIndex,
    Boolean deleted
  ) implements Log {}

  private final static int END_OF_TABLE = -1;
  private final static Storage storage = Storage.getInstance();
  private final static LockTable lockTable = LockTable.getInstance();
  private final Map<String, LockType> locks = new HashMap<>();
  private final List<Log> logs = new ArrayList<>();

  public void commit() {
    logs.clear();
    locks.keySet().forEach(lockTable::unlock);
    locks.clear();
  }

  public void rollback() {
    for (var log: logs.reversed()) {
      switch (log) {
        case SetObjectLog setObjectLog -> storage.setObject(setObjectLog.tableName(), setObjectLog.rowIndex(), setObjectLog.columnIndex(), setObjectLog.value());
        case SetDeleteLog setDeleteLog -> storage.setDelete(setDeleteLog.tableName(), setDeleteLog.rowIndex(), setDeleteLog.deleted());
        default -> throw new RuntimeException("internal error");
      }
    }
    logs.clear();
    locks.keySet().forEach(lockTable::unlock);
    locks.clear();
  }

  public void createTable(String tableName) {
    storage.createTable(tableName);
  }

  public void renameTable(String oldTableName, String newTableName) {
    storage.renameTable(oldTableName, newTableName);
  }

  public void dropTable(String tableName) {
    storage.dropTable(tableName);
  }

  public void addColumn(String tableName) {
    storage.addColumn(tableName);
  }

  public void dropColumn(String tableName, int columnIndex) {
    storage.dropColumn(tableName, columnIndex);
  }

  public int size(String tableName) {
    slock(tableName, END_OF_TABLE);
    return storage.size(tableName);
  }

  public int insert(String tableName, int columnCount) {
    xlock(tableName, END_OF_TABLE);
    return storage.insert(tableName, columnCount);
  }

  public Object getObject(String tableName, int rowIndex, int columnIndex) {
    slock(tableName, rowIndex);
    return storage.getObject(tableName, rowIndex, columnIndex);
  }

  public void setObject(String tableName, int rowIndex, int columnIndex, Object value) {
    xlock(tableName, rowIndex);
    var currentValue = storage.getObject(tableName, rowIndex, columnIndex);
    logs.add(new SetObjectLog(tableName, rowIndex, columnIndex, currentValue));
    storage.setObject(tableName, rowIndex, columnIndex, value);
  }

  public void setDelete(String tableName, int rowIndex, boolean deleted) {
    xlock(tableName, rowIndex);
    var currentValue = isDeleted(tableName, rowIndex);
    logs.add(new SetDeleteLog(tableName, rowIndex, currentValue));
    storage.setDelete(tableName, rowIndex, deleted);
  }

  public boolean isDeleted(String tableName, int rowIndex) {
    slock(tableName, rowIndex);
    return storage.isDeleted(tableName, rowIndex);
  }

  private String getKey(String tableName, int rowIndex) {
    return tableName + ":" + rowIndex;
  }

  private void slock(String tableName, int rowIndex) {
    var key = getKey(tableName, rowIndex);
    if (locks.get(key) == null) {
      lockTable.slock(key);
      locks.put(key, LockType.S);
    }
  }

  private void xlock(String tableName, int rowIndex) {
    var key = getKey(tableName, rowIndex);
    if (locks.get(key) != LockType.X) {
      slock(tableName, rowIndex);
      lockTable.xlock(key);
      locks.put(key, LockType.X);
    }
  }

}
