package yudb.sql.expr;

import java.util.List;

import yudb.scan.Scan;

public class AsExpr implements Expr {

  private final String alias;
  private final Expr expr;

  public AsExpr(String alias, Expr expr) {
    this.alias = alias;
    this.expr = expr;
  }

  @Override
  public Object evaluate(Scan scan) {
    return expr.evaluate(scan);
  }

  @Override
  public List<Object> evaluateList(Scan scan) {
    return List.of(evaluate(scan));
  }

  @Override
  public String toString() {
    return alias;
  }

}
