package yudb.sql.expr;

import java.util.List;

import yudb.scan.Scan;

public class AggrExpr implements Expr {

  private final String fn;
  private final Expr expr;
  private Double value;
  private Integer count = 0;

  public AggrExpr(String fn, Expr expr) {
    this.fn = fn;
    this.expr = expr;
    reset();
  }

  public void reset() {
    value = switch (fn) {
      case "min" -> Double.MAX_VALUE;
      case "max" -> -Double.MAX_VALUE;
      default -> 0.0;
    };
    count = 0;
  }

  public void update(Scan scan) {
    Object obj = expr.evaluate(scan);
    if (obj != null) {
      Double newValue = ((Number) obj).doubleValue();
      value = switch (fn) {
        case "min" -> Math.min(value, newValue);
        case "max" -> Math.max(value, newValue);
        default -> value + newValue;
      };
      count += 1;
    }
  }

  @Override
  public Object evaluate(Scan scan) {
    return switch (fn) {
      case "avg" -> value / Math.max(count, 1);
      case "count" -> count;
      default -> value;
    };
  }

  @Override
  public List<Object> evaluateList(Scan scan) {
    return List.of(evaluate(scan));
  }

  @Override
  public String toString() {
    return fn + "(" + expr + ")";
  }

}
