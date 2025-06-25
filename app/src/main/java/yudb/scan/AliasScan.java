package yudb.scan;

import java.util.List;

import yudb.engine.Transaction;

public class AliasScan extends Scan {
    private final Scan scan;
    private final List<String> columnLabels;

    public AliasScan(Scan scan, String tableAlias) {
      this.scan = scan;
      this.columnLabels = scan.getColumnLabels().stream()
          .map(columnLabel -> columnLabel.substring(columnLabel.indexOf('.') + 1))
          .map(columnLabel -> tableAlias + "." + columnLabel)
          .toList();
      this.beforeFirst();
    }

    @Override
    public Transaction getTransaction() {
      return scan.getTransaction();
    }

    @Override
    public void beforeFirst() {
      scan.beforeFirst();
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
      if (hasColumnLabel(columnLabel)) {
        columnLabel = columnLabel.substring(columnLabel.indexOf('.') + 1);
        return scan.getObject(columnLabel);
      }
      return null;
    }

    @Override
    public boolean next() {
      return scan.next();
    }

}
