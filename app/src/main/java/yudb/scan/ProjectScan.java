package yudb.scan;

import java.util.ArrayList;
import java.util.List;

import yudb.engine.Transaction;
import yudb.sql.expr.Expr;

public class ProjectScan extends Scan {

  private final Scan scan;
  private final List<String> columnLabels = new ArrayList<>();

  public ProjectScan(Scan scan, List<Expr> selects) {
    this.scan = scan;

    var allColumnLabels = scan.getColumnLabels();
    for (var select: selects) {
      var columnLabel = select.toString().toLowerCase();
      if (columnLabel.endsWith(".*")) {
        var suffix = columnLabel.substring(0, columnLabel.length() - 1);
        columnLabels.addAll(allColumnLabels.stream().filter(s -> s.startsWith(suffix)).toList());
      } else if (columnLabel.endsWith("*")) {
        columnLabels.addAll(allColumnLabels.stream().filter(s -> s.contains(".")).toList());
      } else {
        columnLabels.add(columnLabel);
      }
    }
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
    return columnLabels.contains(columnLabel);
  }

  @Override
  public List<String> getColumnLabels() {
    return columnLabels;
  }

  @Override
  public Object getObject(String columnLabel) {
    return scan.getObject(columnLabel);
  }

  @Override
  public boolean next() {
    return scan.next();
  }

}
