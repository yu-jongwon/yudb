package yudb.jdbc;

import java.sql.SQLException;

import yudb.engine.Transaction;
import yudb.jdbc.adapter.ConnectionAdapter;

public class Connection extends ConnectionAdapter {

  private Transaction tx = new Transaction();

  @Override
  public DatabaseMetaData getMetaData() throws SQLException {
    return new DatabaseMetaData(this);
  }

  @Override
  public Statement createStatement() throws SQLException {
    return new Statement(this);
  }

  Transaction getTransaction() {
    return tx;
  }

  @Override
  public void commit() throws SQLException {
    tx.commit();
    tx = new Transaction();
  }

  @Override
  public void rollback() throws SQLException {
    tx.rollback();
    tx = new Transaction();
  }

  @Override
  public void close() throws SQLException {
    tx.rollback();
  }

}
