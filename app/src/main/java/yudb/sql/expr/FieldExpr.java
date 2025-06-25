package yudb.sql.expr;

import java.util.List;

import yudb.scan.Scan;

public class FieldExpr implements Expr {

  private final String name;

  public FieldExpr(String name) {
    this.name = name;
  }

  @Override
  public Object evaluate(Scan scan) {
    if (name.equals("*")) {
      return true;
    }
    return scan.getObject(name);
  }

  @Override
  public List<Object> evaluateList(Scan scan) {
    return List.of(evaluate(scan));
  }

  @Override
  public String toString() {
    return name;
  }

}
