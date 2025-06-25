package yudb.jdbc.adapter;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ResultSetMetaDataAdapter implements ResultSetMetaData {

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'unwrap'");
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return false;
  }

  @Override
  public int getColumnCount() throws SQLException {
    return 0;
  }

  @Override
  public boolean isAutoIncrement(int column) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'isAutoIncrement'");
  }

  @Override
  public boolean isCaseSensitive(int column) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'isCaseSensitive'");
  }

  @Override
  public boolean isSearchable(int column) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'isSearchable'");
  }

  @Override
  public boolean isCurrency(int column) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'isCurrency'");
  }

  @Override
  public int isNullable(int column) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'isNullable'");
  }

  @Override
  public boolean isSigned(int column) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'isSigned'");
  }

  @Override
  public int getColumnDisplaySize(int column) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'getColumnDisplaySize'");
  }

  @Override
  public String getColumnLabel(int column) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'getColumnLabel'");
  }

  @Override
  public String getColumnName(int column) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'getColumnName'");
  }

  @Override
  public String getSchemaName(int column) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'getSchemaName'");
  }

  @Override
  public int getPrecision(int column) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'getPrecision'");
  }

  @Override
  public int getScale(int column) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'getScale'");
  }

  @Override
  public String getTableName(int column) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'getTableName'");
  }

  @Override
  public String getCatalogName(int column) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'getCatalogName'");
  }

  @Override
  public int getColumnType(int column) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'getColumnType'");
  }

  @Override
  public String getColumnTypeName(int column) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'getColumnTypeName'");
  }

  @Override
  public boolean isReadOnly(int column) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'isReadOnly'");
  }

  @Override
  public boolean isWritable(int column) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'isWritable'");
  }

  @Override
  public boolean isDefinitelyWritable(int column) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'isDefinitelyWritable'");
  }

  @Override
  public String getColumnClassName(int column) throws SQLException {
    throw new UnsupportedOperationException("Unimplemented method 'getColumnClassName'");
  }

}
