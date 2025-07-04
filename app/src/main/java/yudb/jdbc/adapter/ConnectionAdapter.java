package yudb.jdbc.adapter;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public abstract class ConnectionAdapter implements Connection {

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'unwrap'");
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'isWrapperFor'");
  }

  @Override
  public Statement createStatement() throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'createStatement'");
  }

  @Override
  public PreparedStatement prepareStatement(String sql) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'prepareStatement'");
  }

  @Override
  public CallableStatement prepareCall(String sql) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'prepareCall'");
  }

  @Override
  public String nativeSQL(String sql) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'nativeSQL'");
  }

  @Override
  public void setAutoCommit(boolean autoCommit) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'setAutoCommit'");
  }

  @Override
  public boolean getAutoCommit() throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'getAutoCommit'");
  }

  @Override
  public void commit() throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'commit'");
  }

  @Override
  public void rollback() throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'rollback'");
  }

  @Override
  public void close() throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'close'");
  }

  @Override
  public boolean isClosed() throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'isClosed'");
  }

  @Override
  public DatabaseMetaData getMetaData() throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'getMetaData'");
  }

  @Override
  public void setReadOnly(boolean readOnly) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'setReadOnly'");
  }

  @Override
  public boolean isReadOnly() throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'isReadOnly'");
  }

  @Override
  public void setCatalog(String catalog) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'setCatalog'");
  }

  @Override
  public String getCatalog() throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'getCatalog'");
  }

  @Override
  public void setTransactionIsolation(int level) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'setTransactionIsolation'");
  }

  @Override
  public int getTransactionIsolation() throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'getTransactionIsolation'");
  }

  @Override
  public SQLWarning getWarnings() throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'getWarnings'");
  }

  @Override
  public void clearWarnings() throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'clearWarnings'");
  }

  @Override
  public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'createStatement'");
  }

  @Override
  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
      throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'prepareStatement'");
  }

  @Override
  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'prepareCall'");
  }

  @Override
  public Map<String, Class<?>> getTypeMap() throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'getTypeMap'");
  }

  @Override
  public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'setTypeMap'");
  }

  @Override
  public void setHoldability(int holdability) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'setHoldability'");
  }

  @Override
  public int getHoldability() throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'getHoldability'");
  }

  @Override
  public Savepoint setSavepoint() throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'setSavepoint'");
  }

  @Override
  public Savepoint setSavepoint(String name) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'setSavepoint'");
  }

  @Override
  public void rollback(Savepoint savepoint) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'rollback'");
  }

  @Override
  public void releaseSavepoint(Savepoint savepoint) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'releaseSavepoint'");
  }

  @Override
  public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
      throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'createStatement'");
  }

  @Override
  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
      int resultSetHoldability) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'prepareStatement'");
  }

  @Override
  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
      int resultSetHoldability) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'prepareCall'");
  }

  @Override
  public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'prepareStatement'");
  }

  @Override
  public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'prepareStatement'");
  }

  @Override
  public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'prepareStatement'");
  }

  @Override
  public Clob createClob() throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'createClob'");
  }

  @Override
  public Blob createBlob() throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'createBlob'");
  }

  @Override
  public NClob createNClob() throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'createNClob'");
  }

  @Override
  public SQLXML createSQLXML() throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'createSQLXML'");
  }

  @Override
  public boolean isValid(int timeout) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'isValid'");
  }

  @Override
  public void setClientInfo(String name, String value) throws SQLClientInfoException {
    throw new UnsupportedOperationException("Unimplemented method 'setClientInfo'");
  }

  @Override
  public void setClientInfo(Properties properties) throws SQLClientInfoException {
    throw new UnsupportedOperationException("Unimplemented method 'setClientInfo'");
  }

  @Override
  public String getClientInfo(String name) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'getClientInfo'");
  }

  @Override
  public Properties getClientInfo() throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'getClientInfo'");
  }

  @Override
  public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'createArrayOf'");
  }

  @Override
  public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'createStruct'");
  }

  @Override
  public void setSchema(String schema) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'setSchema'");
  }

  @Override
  public String getSchema() throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'getSchema'");
  }

  @Override
  public void abort(Executor executor) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'abort'");
  }

  @Override
  public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'setNetworkTimeout'");
  }

  @Override
  public int getNetworkTimeout() throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'getNetworkTimeout'");
  }

}
