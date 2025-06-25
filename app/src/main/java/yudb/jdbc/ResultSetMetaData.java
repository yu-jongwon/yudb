package yudb.jdbc;

import java.sql.SQLException;
import java.util.List;

import yudb.jdbc.adapter.ResultSetMetaDataAdapter;

public class ResultSetMetaData extends ResultSetMetaDataAdapter {

  List<String> columnLabels;

  public ResultSetMetaData(List<String> columnLabels) {
    this.columnLabels = columnLabels;
  }

  @Override
  public int getColumnCount() throws SQLException {
    return columnLabels.size();
  }

  @Override
  public String getColumnLabel(int column) throws SQLException {
    return columnLabels.get(column - 1);
  }

  @Override
  public String getColumnName(int column) throws SQLException {
    return columnLabels.get(column - 1);
  }

}
