package yudb.jdbc;

import java.sql.SQLException;

import yudb.jdbc.adapter.StatementAdapter;
import yudb.sql.Parser;
import yudb.sql.ddl.AddColumnQuery;
import yudb.sql.ddl.CreateTableQuery;
import yudb.sql.ddl.DropColumnQuery;
import yudb.sql.ddl.DropTableQuery;
import yudb.sql.ddl.RenameColumnQuery;
import yudb.sql.ddl.RenameTableQuery;
import yudb.sql.mdl.DeleteQuery;
import yudb.sql.mdl.InsertQuery;
import yudb.sql.mdl.SelectQuery;
import yudb.sql.mdl.UpdateQuery;
import yudb.sql.tcl.CommitQuery;
import yudb.sql.tcl.RollbackQuery;

public class Statement extends StatementAdapter {

  private Connection conn;
  private ResultSet resultSet;
  private int updateCount;

  Statement(Connection conn) {
    this.conn = conn;
  }

  @Override
  public boolean execute(String sql) throws SQLException {
    resultSet = null;
    updateCount = 0;

    var query = Parser.parse(sql);
    switch (query) {
      case CommitQuery       c -> conn.commit();
      case RollbackQuery     c -> conn.rollback();
      case CreateTableQuery  c -> updateCount = c.execute(conn.getTransaction()) ? 1 : 0;
      case RenameTableQuery  c -> updateCount = c.execute(conn.getTransaction()) ? 1 : 0;
      case DropTableQuery    c -> updateCount = c.execute(conn.getTransaction()) ? 1 : 0;
      case AddColumnQuery    c -> updateCount = c.execute(conn.getTransaction()) ? 1 : 0;
      case RenameColumnQuery c -> updateCount = c.execute(conn.getTransaction()) ? 1 : 0;
      case DropColumnQuery   c -> updateCount = c.execute(conn.getTransaction()) ? 1 : 0;
      case SelectQuery       s -> resultSet   = new ResultSet(s.createScan(conn.getTransaction()));
      case InsertQuery       i -> updateCount = i.execute(conn.getTransaction());
      case UpdateQuery       u -> updateCount = u.execute(conn.getTransaction());
      case DeleteQuery       d -> updateCount = d.execute(conn.getTransaction());
      default -> throw new SQLException("invalid query for execute: " + query);
    };
    return query instanceof SelectQuery;
  }

  @Override
  public ResultSet getResultSet() throws SQLException {
    return resultSet;
  }

  @Override
  public int getUpdateCount() throws SQLException {
    return updateCount;
  }

  @Override
  public void setMaxRows(int max) throws SQLException {}

  @Override
  public void close() throws SQLException {}

}
