package yudb.sql.expr;

import java.util.List;

import yudb.scan.Scan;

public interface Expr {

  public abstract Object evaluate(Scan scan);

  public abstract List<Object> evaluateList(Scan scan);

  public abstract String toString();

}
