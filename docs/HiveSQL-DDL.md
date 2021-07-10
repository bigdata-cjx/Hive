# database
```
CREATE (DATABASE|SCHEMA) [IF NOT EXISTS] database_name
  [COMMENT database_comment]
  [LOCATION hdfs_path]
  [WITH DBPROPERTIES (property_name=property_value, ...)];
1.create database db_hive;
  CREATE DATABASE IF NOT EXISTS db_hive  //标准方式
  CREATE DATABASE IF NOT EXISTS db_kfk LOCATION  '/user/cjx/cjxwarehouse/db_cjx'; //自定义数据存放位置

DROP (DATABASE|SCHEMA) [IF EXISTS] database_name [RESTRICT|CASCADE];
2.drop database
  drop database IF EXISTS db_kfk; //标准方式
  drop database IF EXISTS db_hive cascade; //直接删除数据库，包括其中的数据表

USE database_name;
3.use database
```
# table
## Hive中数据库表的创建方式（三种）
```
CREATE [TEMPORARY] [EXTERNAL] TABLE [IF NOT EXISTS] [db_name.]table_name    -- (Note: TEMPORARY available in Hive 0.14.0 and later)
  [(col_name data_type [column_constraint_specification] [COMMENT col_comment], ... [constraint_specification])]
  [COMMENT table_comment]
  [PARTITIONED BY (col_name data_type [COMMENT col_comment], ...)]
  [CLUSTERED BY (col_name, col_name, ...) [SORTED BY (col_name [ASC|DESC], ...)] INTO num_buckets BUCKETS]
  [SKEWED BY (col_name, col_name, ...)                  -- (Note: Available in Hive 0.10.0 and later)]
     ON ((col_value, col_value, ...), (col_value, col_value, ...), ...)
     [STORED AS DIRECTORIES]
  [
   [ROW FORMAT row_format] 
   [STORED AS file_format]
     | STORED BY 'storage.handler.class.name' [WITH SERDEPROPERTIES (...)]  -- (Note: Available in Hive 0.6.0 and later)
  ]
  [LOCATION hdfs_path]
  [TBLPROPERTIES (property_name=property_value, ...)]   -- (Note: Available in Hive 0.6.0 and later)
  [AS select_statement];   -- (Note: Available in Hive 0.5.0 and later; not supported for external tables)
 
CREATE [TEMPORARY] [EXTERNAL] TABLE [IF NOT EXISTS] [db_name.]table_name
  LIKE existing_table_or_view_name
  [LOCATION hdfs_path];
```
### 第一种
```
CREATE  TABLE IF NOT EXISTS db_name.table_name  
  (col_name data_type  )
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY ','
  STORED AS TEXTFILE;

eg:
CREATE TABLE student (
  userid string,
  username string)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;
```
### 第二种(复制表结构)
```
CREATE  TABLE IF NOT EXISTS db_name.table_name
  LIKE existing_table_or_view_name

eg:
CREATE  TABLE IF NOT EXISTS db_hive.stu
  LIKE db_hive.student
```
### 第三种(通过查询得到的结果创建表，复制表结构及其数据)
```
CREATE  TABLE IF NOT EXISTS db_name.table_name  
AS select * from table

eg：
CREATE  TABLE IF NOT EXISTS db_hive.stu  
AS select * from db_hive.student

eg：(复制表)
CREATE  TABLE IF NOT EXISTS db_hive.stu  
AS db_hive.student
```
## Hive表的操作及表创建的场景
### Hive数据分析完成之后的结果我们会保存的地方
1. 存储到HDFS上
2. 存储在hive的表中（临时表）
```
2. drop table
   DROP TABLE IF EXISTS table_name;
3. truncate table 
   TRUNCATE TABLE table_name;
load data local inpath '/opt/datas/student.txt' into table student;//加载本地文件中的数据到表
load data local inpath '/opt/datas/student.txt' overwrite into table student ;
insert into table stu select * from student;//将查询到的结果插入到表

load data inpath '/user/kfk/datas/student.txt' overwrite into table student;//加载HDFS中的数据到表。会将HDFS中的该文件移动到该表所属的Hive数据库数据所在的HDFS文件目录下。
会把HDFS的 /user/kfk/datas/student.txt 移动到 /user/hive/warehouse/student 下
```
# Hive中表的类型
1. 管理表/内部表: MANAGED_TABLE
2. 外部表: EXTERNAL_TABLE
```
CREATE  EXTERNAL TABLE IF NOT EXISTS db_name.table_name  
	  (col_name data_type  )
	  ROW FORMAT DELIMITED
	  FIELDS TERMINATED BY ','
	  STORED AS TEXTFILE;

eg:
CREATE EXTERNAL TABLE student_ext (
userid string,
username string)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;
     
hive表，两份数据：1.HDFS源数据   2.metastore数据/元数据
```
## 管理表和外部表的区别以及应用场景
### 管理表和外部表的区别
```
drop table:
管理表 -> HDFS(删除) + metastore元数据(删除)
外部表 -> HDFS(不删除) + metastore元数据(删除)
```    
### 例子(网站的日志数据)
```
统计分析(日志数据) -> HDFS(共享日志数据)

eg:
   CREATE EXTERNAL TABLE student_ext_1 (
          userid string,
          username string)
        ROW FORMAT DELIMITED
        FIELDS TERMINATED BY ','
        STORED AS TEXTFILE
        LOCATION '/user/kfk/kfkwarehouse/student'

用户推荐(日志数据) -> HDFS(共享日志数据)

eg：
   CREATE EXTERNAL TABLE student_ext_2 (
      userid string,
      username string)
    ROW FORMAT DELIMITED
    FIELDS TERMINATED BY ','
    STORED AS TEXTFILE
    LOCATION '/user/kfk/kfkwarehouse/student'
```

