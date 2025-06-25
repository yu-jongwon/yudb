package yudb.sql.expr;

import java.util.List;

import yudb.scan.Scan;

public class LikeExpr implements Expr {

  private final Expr lhs;
  private final Expr rhs;

  public LikeExpr(Expr lhs, Expr rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  @Override
  public Object evaluate(Scan scan) {
    var left = lhs.evaluate(scan);
    var right = rhs.evaluate(scan);
    if (left != null && left instanceof String && right != null && right instanceof String) {
      var regex = ((String) right)
        .replace(".", "\\.")
        .replace("_", ".")
        .replace("%", ".*");
      return ((String) left).matches(regex);
    }
    return null;
  }

  @Override
  public List<Object> evaluateList(Scan scan) {
    return List.of(evaluate(scan));
  }

  @Override
  public String toString() {
    return lhs + " like " + rhs;
  }

}
