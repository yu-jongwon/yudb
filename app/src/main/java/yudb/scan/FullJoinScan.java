package yudb.scan;

import java.util.List;
import java.util.stream.Stream;

import yudb.engine.Transaction;
import yudb.sql.expr.Expr;

public class FullJoinScan extends Scan {

  private final Scan left;
  private final Scan right;
  private final Expr expr;
  private boolean leftPhase = true;
  private boolean hasNext = false;
  private boolean matched = false;
  private boolean pendingEmitUnmatchedRow = false;

  public FullJoinScan(Scan left, Scan right, Expr expr) {
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
    leftPhase = true;
    hasNext = left.next();
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
    if (leftPhase) {
      if (left.hasColumnLabel(columnLabel)) {
        return left.getObject(columnLabel);
      }
      if (pendingEmitUnmatchedRow) {
        return null;
      }
      return right.getObject(columnLabel);
    } else {
      if (right.hasColumnLabel(columnLabel)) {
        return right.getObject(columnLabel);
      }
      if (pendingEmitUnmatchedRow) {
        return null;
      }
      return left.getObject(columnLabel);
    }
  }

  @Override
  public boolean next() {
    if (leftPhase) {
      while (hasNext) {
        if (right.next()) {
          if (Boolean.TRUE.equals(expr.evaluate(this))) {
            matched = true;
            return true;
          }
        } else {
          if (matched == false && pendingEmitUnmatchedRow == false) {
            pendingEmitUnmatchedRow = true;
            return true;
          }

          hasNext = left.next();
          right.beforeFirst();
          matched = false;
          pendingEmitUnmatchedRow = false;
        }
      }

      left.beforeFirst();
      right.beforeFirst();
      leftPhase = false;
      hasNext = right.next();
      matched = false;
      pendingEmitUnmatchedRow = false;
    }

    while (hasNext) {
      if (left.next()) {
        if (Boolean.TRUE.equals(expr.evaluate(this))) {
          left.beforeFirst();
          hasNext = right.next();
        }
      } else {
        if (pendingEmitUnmatchedRow == false) {
          pendingEmitUnmatchedRow = true;
          return true;
        }

        left.beforeFirst();
        hasNext = right.next();
        pendingEmitUnmatchedRow = false;
      }
    }

    return false;
  }

}
