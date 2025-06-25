package yudb.scan;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import yudb.engine.Transaction;
import yudb.sql.expr.AggrExpr;
import yudb.sql.expr.Expr;
import yudb.sql.mdl.SelectQuery.Order;

public class GroupScan extends Scan {

  private final Scan scan;
  private final List<String> columnLabels;
  private final List<Object> columnValues = new ArrayList<>();
  private final List<AggrExpr> aggrs;
  private final List<Expr> groupExprs;
  private boolean hasNext;

  public GroupScan(Scan scan, List<AggrExpr> aggrs, List<Expr> groupExprs) {
    this.scan = new SortScan(scan, groupExprs.stream().map(Order::new).toList());
    this.columnLabels = scan.getColumnLabels();
    this.aggrs = aggrs;
    this.groupExprs = groupExprs;
    this.beforeFirst();
  }

  @Override
  public Transaction getTransaction() {
    return scan.getTransaction();
  }

  @Override
  public void beforeFirst() {
    scan.beforeFirst();
    columnValues.clear();
    hasNext = scan.next();
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
    var columnIndex = indexOf(columnLabels, columnLabel);
    if (columnIndex == -1) {
      return null;
    }
    return columnValues.get(columnIndex);
  }

  @Override
  public boolean next() {
    if (hasNext == false) {
      return false;
    }

    for (var aggr: aggrs) {
      aggr.reset();
    }

    while (true) {
      for (var aggr: aggrs) {
        aggr.update(scan);
      }

      for (var columnLabel: scan.getColumnLabels()) {
        var value = scan.getObject(columnLabel);
        columnValues.add(value);
      }

      List<Object> groupValues = new ArrayList<>();
      for (var expr: groupExprs) {
        var value = expr.evaluate(scan);
        groupValues.add(value);
      }

      hasNext = scan.next();
      if (hasNext == false) {
        return true;
      }

      List<Object> newGroupValues = new ArrayList<>();
      for (var expr: groupExprs) {
        var value = expr.evaluate(scan);
        newGroupValues.add(value);
      }

      if (Objects.deepEquals(groupValues, newGroupValues) == false) {
        return true;
      }
    }
  }

}
