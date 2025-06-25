package yudb;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;

public class App {
  public static void main(String[] args) {
    try (
      var conn = DriverManager.getConnection("jdbc:yudb:");
      var stmt = conn.createStatement();
      var scanner = new Scanner(System.in);
    ) {
      initSampleData(stmt);

      while (true) {
        try {
          System.out.print("\nsql> ");
          var sql = scanner.nextLine().trim();

          if (sql.isEmpty()) {
            continue;
          }

          if (sql.toLowerCase().startsWith("exit")) {
            break;
          }

          if (sql.toLowerCase().startsWith("help")) {
            printSampleQueries();
            continue;
          }

          if (stmt.execute(sql)) {
            System.out.println(stmt.getResultSet());
          } else {
            System.out.println("updated " + stmt.getUpdateCount() + " row(s)");
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void initSampleData(Statement stmt) {
    try {
      var example = List.of(
        List.of("a", 0),
        List.of("b", 1),
        List.of("b", 3),
        List.of("a", 2),
        List.of("b", 5),
        List.of("a", 4),
        List.of("b", 6)
      );
      stmt.execute("create table @example (s, i)");
      for (var row: example) {
        stmt.execute("insert into @example (s, i) values ('" + row.get(0) + "', " + row.get(1) + ")");
      }

      var A = List.of(
        List.of(1, "A1"),
        List.of(2, "A2"),
        List.of(2, "A3")
      );
      stmt.execute("create table @A (id, name)");
      for (var row : A) {
        stmt.execute("insert into @A (id, name) values (" + row.get(0) + ", '" + row.get(1) + "')");
      }

      var B = List.of(
        List.of(2, "B1"),
        List.of(2, "B2"),
        List.of(3, "B3")
      );
      stmt.execute("create table @B (id, value)");
      for (var row : B) {
        stmt.execute("insert into @B (id, value) values (" + row.get(0) + ", '" + row.get(1) + "')");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void printSampleQueries() {
    System.out.println("""
      create table TABLE_NAME (COLUMN_NAME1, COLUMN_NAME2, COLUMN_NAME3);
      drop table TABLE_NAME;
      rename table OLD_TABLE_NAME to NEW_TABLE_NAME;
      alter table OLD_TABLE_NAME rename to NEW_TABLE_NAME;

      alter table TABLE_NAME add column A_COLUMN_NAME;
      alter table TABLE_NAME drop column A_COLUMN_NAME;
      alter table TABLE_NAME rename column OLD_COLUMN_NAME to NEW_COLUMN_NAME;

      commit;
      rollback;

      select * from @tables;
      select * from @columns;
      select * from @example;
      select * from @example offset 2 limit 3;
      select *, sum(i * 2) * 2, avg(i) + 10, min(i), max(i), count(i) + 1 from @example;
      select * from @example order by s, i desc;
      select *, sum(i), min(i), max(i), avg(i), count(i) from @example group by s;
      select *, sum(i), min(i), max(i), avg(i), count(i) from @example group by s having sum(i) > 12;
      select * from @A;
      select * from @B;
      select * from @A cross join @B;
      select * from @A inner join @B on @A.id = @B.id;
      select * from @A left join @B on @A.id = @B.id;
      select * from @A right join @B on @A.id = @B.id;
      select * from @A full join @B on @A.id = @B.id;
      select i + 2 as k, s as a, i from @example order by k, a, i desc;
      select @example.s as a, @example.i from @example where @example.i > 2;
      select m.s as a, m.i from @example m where m.i > 2;
      select *, t.id from (select id, name from @A) as t;
      select 1, 2, 3, (select 4, 5, 6) as t;
      select id, name from @A where id in (1, 3);
      select id, name from @A where id in (select id from @B);
      select id, name from @A where name like '%1';
      select @A.* from @A;
    """);
  }

}
