package yudb.scan;

import java.util.List;

import yudb.engine.Transaction;

public abstract class Scan {

  public Transaction getTransaction() {
    throw new UnsupportedOperationException("Unimplemented method 'getTransaction'");
  }

  public abstract void beforeFirst();

  public abstract boolean hasColumnLabel(String columnLabel);

  public abstract List<String> getColumnLabels();

  public abstract Object getObject(String columnLabel);

  public void setObject(String columnLabel, Object value) {
    throw new UnsupportedOperationException("Unimplemented method 'setObject'");
  }

  public int insert() {
    throw new UnsupportedOperationException("Unimplemented method 'insert'");
  }

  public void delete() {
    throw new UnsupportedOperationException("Unimplemented method 'delete'");
  }

  public abstract boolean next();

  protected int indexOf(List<String> columnLabels, String columnLabel) {
    if (columnLabels.contains(columnLabel)) {
      return columnLabels.indexOf(columnLabel);
    }

    if (columnLabel.contains(".") == false) {
      columnLabel = "." + columnLabel;
      for (int i = 0; i < columnLabels.size(); i++) {
        if (columnLabels.get(i).endsWith(columnLabel)) {
          return i;
        }
      }
    }

    return -1;
  }

}
