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
# 分区表
## 分区概念
将一个大表的数据根据一定的条件拆分成若干个小表，每个小表对应一个分区

Hive中分区表对应一个单独的文件夹，这个这件夹中的数据就是分区数据(在企业实际应用中，hive表基本都要做分区)
## 为什么要分区
1. 数据检索性能最大化
2. 数据并行计算的最大化

eg:
```
订单表：hive表（select * from order）

0001,henry,2018-09-09,product-1,50
0002,chenry,2018-10-09,product-2,50
0003,ben,2018-09-09,product-3,50
0004,cherry,2018-10-09,product-4,50
0005,jone,2018-10-09,product-5,50
0006,lili,2018-09-09,product-6,50
0007,chen,2018-10-09,product-7,50
0008,wiwi,2018-09-09,product-8,50

订单分区表：Hive表(201809)  (select * from order where order_date = 201809)
0001,henry,2018-09-09,product-1,50
0003,ben,2018-09-09,product-1,50
0006,lili,2018-09-09,product-1,50
0008,wiwi,2018-09-09,product-1,50

订单分区表：Hive表(201810)  (select * from order where order_date = 201810)
0002,chenry,2018-10-09,product-2,50
0004,cherry,2018-10-09,product-4,50
0005,jone,2018-10-09,product-5,50
0007,chen,2018-10-09,product-7,50
```
## 创建分区表(标准用法)
### 创建表
```
CREATE  TABLE order_partition (
  userid string,
  username string,
  order_date string,
  product_name string,
  price string
  )
PARTITIONED BY (month string)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;
```
### 加载数据
```
load data local inpath '/opt/datas/order_201809.txt' into  table  order_partition partition(month = '201809')
load data local inpath '/opt/datas/order_201810.txt' into  table  order_partition partition(month = '201810')
```
## no partition
### HDFS上创建数据表的目录
dfs -mkdir -p /user/hive/warehouse/db_hive.db/nopartition;
### 加载数据（put）
dfs -put /opt/datas/order.txt /user/hive/warehouse/db_hive.db/nopartition;
### 创建表
```
CREATE  TABLE nopartition (
  userid string,
  username string,
  order_date string,
  product_name string,
  price string
  )
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;
```
## partition
### HDFS上创建数据表的目录
dfs -mkdir -p /user/hive/warehouse/db_hive.db/order_part/month=201809;
### 加载数据（put）
dfs -put /opt/datas/order_201809.txt /user/hive/warehouse/db_hive.db/order_part/month=201809;
### 创建表
```
CREATE  TABLE order_part (
  userid string,
  username string,
  order_date string,
  product_name string,
  price string
  )
PARTITIONED BY (month string)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;
```
此时还没有创建元数据，查询不到数据
### 创建元数据
alter table order_part add partition(month = '201809');
# Hive分区在企业中的应用
日志数据：每天都有一份，我们要所有的数据加载到Hive表中并进行分析。
Hive表如何设计：
## 分区表（month + day + time）
### 第一种分案（不可取）
#### 创建表
#### 加载数据
load data local inpath '数据源' into table 数据表 partition(month = '01' ,day = '01',time ='00')
## 第二种方案（经常采用的）,采用脚本的方式
### hdfs上创建数据表目录
```
dfs -mkdir -p /user/hive/warehouse/db_hive.db/order_part/month=201809/day=01/time=00;
dfs -mkdir -p /user/hive/warehouse/db_hive.db/order_part/month=201809/day=01/time=01;
dfs -mkdir -p /user/hive/warehouse/db_hive.db/order_part/month=201809/day=01/time=02;
dfs -mkdir -p /user/hive/warehouse/db_hive.db/order_part/month=201809/day=01/time=03;
```
### 加载数据
```
dfs -put /opt/datas/order_201809.txt /user/hive/warehouse/db_hive.db/order_part/month=201809/day=01/time=00;
dfs -put /opt/datas/order_201809.txt /user/hive/warehouse/db_hive.db/order_part/month=201809/day=01/time=01;
dfs -put /opt/datas/order_201809.txt /user/hive/warehouse/db_hive.db/order_part/month=201809/day=01/time=02;
dfs -put /opt/datas/order_201809.txt /user/hive/warehouse/db_hive.db/order_part/month=201809/day=01/time=03;
```
### 分区数据（metastore元数据）
```
alter table 数据表 add partition(month = '201809',day='01',time='00');
alter table 数据表 add partition(month = '201809',day='01',time='01');
alter table 数据表 add partition(month = '201809',day='01',time='02');
alter table 数据表 add partition(month = '201809',day='01',time='03');
alter table 数据表 add partition(month = '201809',day='01',time='04');
alter table 数据表 add partition(month = '201809',day='01',time='05');
alter table 数据表 add partition(month = '201809',day='01',time='06');
```
### 数据分析