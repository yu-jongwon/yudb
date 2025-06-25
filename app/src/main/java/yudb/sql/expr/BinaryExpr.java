package yudb.sql.expr;

import java.util.List;
import java.util.Objects;

import yudb.scan.Scan;

public class BinaryExpr implements Expr {

  private final String op;
  private final Expr lhs;
  private final Expr rhs;

  public BinaryExpr(String op, Expr lhs, Expr rhs) {
    this.op = op;
    this.lhs = lhs;
    this.rhs = rhs;
  }

  @Override
  public Object evaluate(Scan scan) {
    var a = lhs.evaluate(scan);
    var b = rhs.evaluate(scan);

    if (a == null || b == null) {
      return switch (op) {
        case "==" -> Objects.equals(a, b);
        case "!=" -> Objects.equals(a, b) == false;
        default -> null;
      };
    }

    if (a instanceof Boolean && b instanceof Boolean) {
      switch (op) {
        case "and": return (boolean) a && (boolean) b;
        case "or":  return (boolean) a || (boolean) b;
        case "==":  return (boolean) a == (boolean) b;
        case "!=":  return (boolean) a != (boolean) b;
      }
    }

    if (a instanceof String && b instanceof String) {
      switch (op) {
        case "+":  return (String) a + (String) b;
        case "==": return Objects.equals(a, b);
        case "!=": return Objects.equals(a, b) == false;
      }
    }

    if (a instanceof Integer && b instanceof Integer) {
      switch (op) {
        case "+":  return (int) a +  (int) b;
        case "-":  return (int) a -  (int) b;
        case "*":  return (int) a *  (int) b;
        case "/":  return (int) a /  (int) b;
        case "%":  return (int) a %  (int) b;
        case "<":  return (int) a <  (int) b;
        case "<=": return (int) a <= (int) b;
        case ">":  return (int) a >  (int) b;
        case ">=": return (int) a >= (int) b;
        case "==": return (int) a == (int) b;
        case "!=": return (int) a != (int) b;
      }
    }

    if (a instanceof Integer && b instanceof Double) {
      switch (op) {
        case "+":  return (int) a +  (double) b;
        case "-":  return (int) a -  (double) b;
        case "*":  return (int) a *  (double) b;
        case "/":  return (int) a /  (double) b;
        case "%":  return (int) a %  (double) b;
        case "<":  return (int) a <  (double) b;
        case "<=": return (int) a <= (double) b;
        case ">":  return (int) a >  (double) b;
        case ">=": return (int) a >= (double) b;
        case "==": return (int) a == (double) b;
        case "!=": return (int) a != (double) b;
      }
    }

    if (a instanceof Double && b instanceof Integer) {
      switch (op) {
        case "+":  return (double) a +  (int) b;
        case "-":  return (double) a -  (int) b;
        case "*":  return (double) a *  (int) b;
        case "/":  return (double) a /  (int) b;
        case "%":  return (double) a %  (int) b;
        case "<":  return (double) a <  (int) b;
        case "<=": return (double) a <= (int) b;
        case ">":  return (double) a >  (int) b;
        case ">=": return (double) a >= (int) b;
        case "==": return (double) a == (int) b;
        case "!=": return (double) a != (int) b;
      }
    }

    if (a instanceof Double && b instanceof Double) {
      switch (op) {
        case "+":  return (double) a +  (double) b;
        case "-":  return (double) a -  (double) b;
        case "*":  return (double) a *  (double) b;
        case "/":  return (double) a /  (double) b;
        case "%":  return (double) a %  (double) b;
        case "<":  return (double) a <  (double) b;
        case "<=": return (double) a <= (double) b;
        case ">":  return (double) a >  (double) b;
        case ">=": return (double) a >= (double) b;
        case "==": return (double) a == (double) b;
        case "!=": return (double) a != (double) b;
      }
    }

    throw new UnsupportedOperationException(a.getClass().getSimpleName() + " " + op + " " + b.getClass().getSimpleName());
  }

  @Override
  public List<Object> evaluateList(Scan scan) {
    return List.of(evaluate(scan));
  }

  @Override
  public String toString() {
    return lhs + " " + op + " " + rhs;
  }

}
