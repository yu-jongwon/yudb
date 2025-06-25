package yudb.sql.expr;

import java.util.List;

import yudb.scan.Scan;

public class ListExpr implements Expr {

  private final List<Expr> exprs;

  public ListExpr(List<Expr> exprs) {
    this.exprs = exprs;
  }

  @Override
  public Object evaluate(Scan scan) {
    return exprs.get(0).evaluate(scan);
  }

  @Override
  public List<Object> evaluateList(Scan scan) {
    return exprs.stream().flatMap(expr -> expr.evaluateList(scan).stream()).toList();
  }

  @Override
  public String toString() {
    var values = exprs.stream().map(Expr::toString).toList();
    return "(" + String.join(", ", values) + ")";
  }

}
