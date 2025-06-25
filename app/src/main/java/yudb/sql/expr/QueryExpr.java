package yudb.sql.expr;

import java.util.ArrayList;
import java.util.List;

import yudb.scan.Scan;
import yudb.sql.mdl.SelectQuery;

public class QueryExpr implements Expr {

  private final SelectQuery query;

  public QueryExpr(SelectQuery query) {
    this.query = query;
  }

  @Override
  public Object evaluate(Scan scan) {
    var tx = scan.getTransaction();
    var subScan = query.createScan(tx);
    var columnLabel = subScan.getColumnLabels().get(0);
    if (subScan.next()) {
      return subScan.getObject(columnLabel);
    } else {
      return null;
    }
  }

  @Override
  public List<Object> evaluateList(Scan scan) {
    var exprs = new ArrayList<Object>();
    var tx = scan.getTransaction();
    var subScan = query.createScan(tx);
    var columnLabel = subScan.getColumnLabels().get(0);
    while (subScan.next()) {
      exprs.add(subScan.getObject(columnLabel));
    }
    return exprs;
  }

  @Override
  public String toString() {
    return "(" + query.toString() + ")";
  }

}
