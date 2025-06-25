package yudb.jdbc;

import java.sql.SQLException;

import yudb.sql.expr.BinaryExpr;
import yudb.sql.expr.ConstExpr;
import yudb.sql.expr.FieldExpr;
import yudb.jdbc.adapter.DatabaseMetaDataAdapter;
import yudb.scan.FilterScan;
import yudb.scan.Scan;
import yudb.scan.TableScan;

public class DatabaseMetaData extends DatabaseMetaDataAdapter {

  private Connection conn;

  DatabaseMetaData(Connection conn) {
    this.conn = conn;
  }

  @Override
  public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
    Scan scan = TableScan.getTableInfoScan(conn.getTransaction());
    return new ResultSet(scan);
  }

  @Override
  public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
    Scan scan = TableScan.getColumnInfoScan(conn.getTransaction());
    scan = new FilterScan(scan, new BinaryExpr("==", new FieldExpr("table_name"), new ConstExpr(tableNamePattern)));
    return new ResultSet(scan);
  }

}
