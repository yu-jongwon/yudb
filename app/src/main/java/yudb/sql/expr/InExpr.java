package yudb.sql.expr;

import java.util.List;

import yudb.scan.Scan;

public class InExpr implements Expr {

  private final Expr lhs;
  private final Expr rhs;

  public InExpr(Expr lhs, Expr rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  @Override
  public Object evaluate(Scan scan) {
    var right = rhs.evaluateList(scan);
    return right == null ? null : right.contains(lhs.evaluate(scan));
  }

  @Override
  public List<Object> evaluateList(Scan scan) {
    return List.of(evaluate(scan));
  }

  @Override
  public String toString() {
    return lhs + " in " + rhs;
  }

}
