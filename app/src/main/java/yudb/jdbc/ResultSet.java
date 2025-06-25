package yudb.jdbc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import yudb.jdbc.adapter.ResultSetAdapter;
import yudb.scan.Scan;

public class ResultSet extends ResultSetAdapter {

  private Scan scan;
  private ResultSetMetaData resultSetMetaData;

  ResultSet(Scan scan) {
    this.scan = scan;
    this.resultSetMetaData = new ResultSetMetaData(scan.getColumnLabels());
  }

  @Override
  public boolean next() throws SQLException {
    return scan.next();
  }

  @Override
  public Object getObject(String columnLabel) throws SQLException {
    return scan.getObject(columnLabel.toLowerCase());
  }

  @Override
  public Object getObject(int columnIndex) throws SQLException {
    var columnLabel = resultSetMetaData.getColumnLabel(columnIndex);
    return scan.getObject(columnLabel);
  }

  @Override
  public String getString(String columnLabel) throws SQLException {
    return (String) scan.getObject(columnLabel.toLowerCase());
  }

  @Override
  public void close() throws SQLException { }

  @Override
  public ResultSetMetaData getMetaData() throws SQLException {
    return resultSetMetaData;
  }

  @Override
  public String toString() {
    var s = "";
    try {
      var rows = new ArrayList<List<Object>>();
      var colWidths = new int[getMetaData().getColumnCount()];

      rows.add(new ArrayList<Object>());
      for (var i = 1; i <= getMetaData().getColumnCount(); i++) {
        rows.getLast().add(getMetaData().getColumnName(i));
        colWidths[i-1] = Math.max(colWidths[i-1], String.valueOf(getMetaData().getColumnName(i)).length());
      }

      while (next()) {
        rows.add(new ArrayList<Object>());
        for (var i = 1; i <= getMetaData().getColumnCount(); i++) {
          rows.getLast().add(getObject(i));
          colWidths[i-1] = Math.max(colWidths[i-1], String.valueOf(getObject(i)).length());
        }
      }

      for (var j = 0; j < rows.get(0).size(); j++) {
        var format = "%-" + colWidths[j] + "s\t";
        var arg = rows.get(0).get(j);
        s += String.format(format, arg);
      }
      s += '\n';

      for (var j = 0; j < rows.get(0).size(); j++) {
        s += "-".repeat(colWidths[j]) + "\t";
      }
      s += '\n';

      for (int i = 1; i < rows.size(); i++) {
        for (var j = 0; j < rows.get(i).size(); j++) {
          if (rows.get(i).get(j) instanceof Integer) {
            var format = "%" + colWidths[j] + "s\t";
            var arg = rows.get(i).get(j);
            s += String.format(format, arg);
          } else if (rows.get(i).get(j) instanceof Double) {
            var format = "%" + colWidths[j] + "s\t";
            var arg = rows.get(i).get(j);
            s += String.format(format, arg);
          } else {
            var format = "%-" + colWidths[j] + "s\t";
            var arg = String.valueOf(rows.get(i).get(j));
            s += String.format(format, arg);
          }
        }
        s += '\n';
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return s;
  }

}
