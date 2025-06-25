package yudb.scan;

import java.util.ArrayList;
import java.util.List;

import yudb.engine.Transaction;
import yudb.sql.mdl.SelectQuery.Direction;
import yudb.sql.mdl.SelectQuery.Order;

public class SortScan extends Scan {

  private final Scan scan;

  public SortScan(Scan scan, List<Order> orders) {
    var columnLabels = scan.getColumnLabels();
    var tempTables = split(scan, orders, columnLabels);
    this.scan = merge(tempTables, orders, columnLabels);
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
    return scan.hasColumnLabel(columnLabel);
  }

  @Override
  public List<String> getColumnLabels() {
    return scan.getColumnLabels();
  }

  @Override
  public Object getObject(String columnLabel) {
    return scan.getObject(columnLabel);
  }

  @Override
  public boolean next() {
    return scan.next();
  }

  private static List<Scan> split(Scan scan, List<Order> orders, List<String> columnLabels) {
    var result = new ArrayList<Scan>();

    if (scan.next()) {
      var temp = new TempScan(scan.getColumnLabels());
      result.add(temp);
      while (copyRow(columnLabels, scan, temp)) {
        if (lessThan(scan, temp, orders)) {
          temp = new TempScan(scan.getColumnLabels());
          result.add(temp);
        }
      }
    }

    if (result.isEmpty()) {
      result.add(scan);
    }

    return result;
  }

  private static Scan merge(List<Scan> q1, List<Order> orders, List<String> columnLabels) {
    while (q1.size() > 1) {
      var q2 = q1;
      q1 = new ArrayList<Scan>();
      while (q2.size() > 1) {
        var temp = new TempScan(columnLabels);
        q1.add(temp);

        var scan1 = q2.removeFirst();
        var scan2 = q2.removeFirst();

        scan1.beforeFirst();
        scan2.beforeFirst();

        var next1 = scan1.next();
        var next2 = scan2.next();
        while (next1 && next2) {
          if (lessThan(scan1, scan2, orders)) {
            next1 = copyRow(columnLabels, scan1, temp);
          } else {
            next2 = copyRow(columnLabels, scan2, temp);
          }
        }
        while (next1) next1 = copyRow(columnLabels, scan1, temp);
        while (next2) next2 = copyRow(columnLabels, scan2, temp);
      }
      q1.addAll(q2);
    }
    return q1.getFirst();
  }

  private static boolean copyRow(List<String> columnLabels, Scan from, Scan to) {
    to.insert();
    for (var columnLabel: columnLabels) {
      var value = from.getObject(columnLabel);
      to.setObject(columnLabel, value);
    }
    return from.next();
  }

  @SuppressWarnings("unchecked")
  private static boolean lessThan(Scan scan1, Scan scan2, List<Order> orders) {
    for (var orderBy : orders) {
      var value1 = orderBy.expr.evaluate(scan1);
      var value2 = orderBy.expr.evaluate(scan2);
      if (value1 == null || value2 == null) {
        return false;
      }

      var cmp = ((Comparable<Object>) value1).compareTo(value2);
      if (cmp != 0) {
        return (cmp < 0) == (orderBy.direction == Direction.Asc);
      }
    }
    return false;
  }

}
