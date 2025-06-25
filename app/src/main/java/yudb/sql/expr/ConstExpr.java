package yudb.sql.expr;

import java.util.List;

import yudb.scan.Scan;

public class ConstExpr implements Expr {

  private final Object value;

  public ConstExpr(Object value) {
    this.value = value;
  }

  @Override
  public Object evaluate(Scan scan) {
    return value;
  }

  @Override
  public List<Object> evaluateList(Scan scan) {
    return List.of(evaluate(scan));
  }

  @Override
  public String toString() {
    return value.toString();
  }

}
