# YuDB

구현 학습용 데이터 베이스

## 특징
  * 관계형
  * 인메모리
  * 동적 타입
  * JDBC

## 기능
  * create table
  * drop table
  * alter table
  * commit
  * rollback
  * select
  * update
  * delete
  * where
  * group by
  * having
  * order by `asc` `desc`
  * `cross` `inner` `left` `right` `full` join
  * offset
  * limit
  * as
  * 서브 쿼리
  * 집계 함수 `min` `max` `avg` `sum` `count`
  * 연산자 `+` `-` `*` `/` `%` `<` `>` `<=` `>=` `==` `!=` `and` `or` `like` `in`
  * 테이블 선택 `.`

## DDL 샘플 쿼리

```sql
  create table TABLE_NAME (COLUMN_NAME1, COLUMN_NAME2, COLUMN_NAME3);
  drop table TABLE_NAME;
  alter table OLD_TABLE_NAME rename to NEW_TABLE_NAME;

  alter table TABLE_NAME add column A_COLUMN_NAME;
  alter table TABLE_NAME drop column A_COLUMN_NAME;
  alter table TABLE_NAME rename column OLD_COLUMN_NAME to NEW_COLUMN_NAME;
```

## TCL 샘플 쿼리

```sql
  commit;
  rollback;
```

## MDL 샘플 쿼리

  * `@tables` - 테이블을 관리하는 메타 테이블
  * `@columns` - 컬럼을 관리하는 메타 테이블
  * `@samples`, `@A`, `@B` - 내장된 샘플 데이터 테이블

```sql
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
```

## 실행 방법

```bash
> java -jar app/dist/yudb-1.0.jar

sql> select * from @tables;
```

## [DBeaver](https://dbeaver.io/) 로 실행하는 방법

  * `Database` -> `Driver Manager` -> `New` 메뉴에서 다음 설정으로 드라이버 추가
    * Settings
      * Driver Name: YuDB
      * Class Name: yudb.jdbc.Driver
      * URL Template: jdbc:yudb:
      * Embedded: `check`
      * No authentication: `check`
      * Thread safe driver: `check`
    * Libraries
      * Add File: `yudb-1.0.jar` 파일 경로 추가
      * Find Class: yudb.jdbc.Driver
  * `Database` -> `New Database Connection` 메뉴에서 YuDB 드라이버 선택 후 커넥션 생성

## 참고 문헌

  * [Database Design and Implementation, 2020, Sciore, Edward, Springer](https://link.springer.com/book/10.1007/978-3-030-33836-7)
