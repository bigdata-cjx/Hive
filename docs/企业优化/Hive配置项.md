# hive.fetch.task.conversion
```
<property>
    <name>hive.fetch.task.conversion</name>
    <value>more</value>
    <description>
      Expects one of [none, minimal, more].
      Some select queries can be converted to single FETCH task minimizing latency.
      Currently the query should be single sourced not having any subquery and should not have
      any aggregations or distincts (which incurs RS), lateral views and joins.
      0. none : disable hive.fetch.task.conversion
      1. minimal : SELECT STAR, FILTER on partition columns, LIMIT only
      2. more    : SELECT, FILTER, LIMIT only (support TABLESAMPLE and virtual columns)
    </description>
</property>
```
## none
所有的语句都走MR
```
hive (default)> set hive.fetch.task.conversion = none;
// 走MR
hive (default)> select * from test;
```
## minimal
```
hive (default)> set hive.fetch.task.conversion = minimal;
// 不走MR
hive (default)> select * from test;
```
## more
```
hive (default)> set hive.fetch.task.conversion = more;
// 不走MR
hive (default)> select * from test;
```