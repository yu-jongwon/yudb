package yudb.scan;

import java.util.List;
import java.util.stream.Stream;

import yudb.engine.Transaction;
import yudb.sql.expr.Expr;

public class RightJoinScan extends Scan {

  private final Scan left;
  private final Scan right;
  private final Expr expr;
  private boolean hasNext = false;
  private boolean matched = false;
  private boolean pendingEmitUnmatchedRow = false;

  public RightJoinScan(Scan left, Scan right, Expr expr) {
    this.left = left;
    this.right = right;
    this.expr = expr;
    this.beforeFirst();
  }

  @Override
  public Transaction getTransaction() {
    return left.getTransaction();
  }

  @Override
  public void beforeFirst() {
    left.beforeFirst();
    right.beforeFirst();
    hasNext = right.next();
    matched = false;
    pendingEmitUnmatchedRow = false;
  }

  @Override
  public boolean hasColumnLabel(String columnLabel) {
    return left.hasColumnLabel(columnLabel) || right.hasColumnLabel(columnLabel);
  }

  @Override
  public List<String> getColumnLabels() {
    return Stream.concat(left.getColumnLabels().stream(), right.getColumnLabels().stream()).toList();
  }

  @Override
  public Object getObject(String columnLabel) {
    if (right.hasColumnLabel(columnLabel)) {
      return right.getObject(columnLabel);
    }
    if (pendingEmitUnmatchedRow) {
      return null;
    }
    return left.getObject(columnLabel);
  }

  @Override
  public boolean next() {
    while (hasNext) {
      if (left.next()) {
        if (Boolean.TRUE.equals(expr.evaluate(this))) {
          matched = true;
          return true;
        }
      } else {
        if (matched == false && pendingEmitUnmatchedRow == false) {
          pendingEmitUnmatchedRow = true;
          return true;
        }

        hasNext = right.next();
        left.beforeFirst();
        matched = false;
        pendingEmitUnmatchedRow = false;
      }
    }
    return false;
  }

}
