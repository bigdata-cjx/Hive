# Hive常用命令
1. show databases;
2. create databases db_name;
3. drop database db_name;
4. use db_name;
5. select * from test;
## 创建表
```
create table test(
id int,
name string
) 
ROW FORMAT delimited 
FIELDS TERMINATED BY '\t'
stored as textfile 
;
```
## 查看表结构
desc test;
### 查看表的详细信息
1. desc extended u_data;
2. desc formatted u_data;
## 加载数据到表
load data local inpath '/opt/modules/datas/test.txt' into table test;

# 数据准备
## 下载数据
https://grouplens.org/datasets/movielens/
```
docker cp ml-latest-small b4c3d4354fae:/opt/datas/
root@b4c3d4354fae:/opt/datas/ml-latest-small# ll
total 3244
drwxr-xr-x 2 1000 1000    4096 Sep 26  2018 ./
drwxr-xr-x 3 root root    4096 Jul  7 13:28 ../
-rw-r--r-- 1 1000 1000    8342 Sep 26  2018 README.txt
-rw-r--r-- 1 1000 1000  197979 Sep 26  2018 links.csv
-rw-r--r-- 1 1000 1000  494431 Sep 26  2018 movies.csv
-rw-r--r-- 1 1000 1000 2483723 Sep 26  2018 ratings.csv
-rw-r--r-- 1 1000 1000  118660 Sep 26  2018 tags.csv
root@b4c3d4354fae:/opt/datas/ml-latest-small# pwd
/opt/datas/ml-latest-small
```
## 创建表结构并加载数据
```
CREATE TABLE u_data (
  userid INT,
  movieid INT,
  rating STRING,
  unixtime STRING)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;

load data local inpath '/opt/datas/ml-latest-small/ratings.csv' into table u_data;
```
## 操作
```
hive (default)> select count(*) from u_data;

Stage-Stage-1: Map: 1  Reduce: 1   Cumulative CPU: 2.09 sec   HDFS Read: 2491603 HDFS Write: 106 SUCCESS
Total MapReduce CPU Time Spent: 2 seconds 90 msec
OK
_c0
100837
Time taken: 13.873 seconds, Fetched: 1 row(s)
```
## 函数
1. show functions;
2. desc function upper;
3. desc function extended upper;
4. select userid,upper(username) uppper_name from student;
# Hive常用的配置属性
## Hive数据仓库位置配置
```
<property>
<name>hive.metastore.warehouse.dir</name>
<value>/user/hive/warehouse</value>
<description>location of default database for the warehouse</description>
</property>

$ $HADOOP_HOME/bin/hadoop fs -mkdir       /user/hive/warehouse
$ $HADOOP_HOME/bin/hadoop fs -chmod g+w   /user/hive/warehouse
```
## Hive运行日志的配置
```
conf/hive-log4j.properties文件

hive.log.dir=/opt/modules/hive-1.1.0-cdh5.5.0/logs
hive.log.file=hive.log
```
## Hive运行日志的级别
```
hive.root.logger=info,DRFA
```
## 在cli命令行上显示当前数据库名称，以及查询表的表头信息
```
<property>
    <name>hive.cli.print.header</name>
    <value>true</value>
</property>

<property>
    <name>hive.cli.print.current.db</name>
    <value>true</value>
</property>
```
## 在启动hive时设置配属属性项信息
```
bin/hive --hiveconf <property=value> 

eg:   bin/hive --hiveconf  hive.cli.print.header=false

此种方式的设置，仅仅在当前会话session中有效
```
## 查看hive当前所有的配置信息
hive (default)> set;
### 查看某一项配置的值
hive (default)> set hive.cli.print.header;
### 修改某一项的值
hive (default)> set hive.cli.print.header=false;
## hive -help
hive (default)> bin/hive -help
## 配置优先级
set  -> --hiveconf -> hive-site.xml -> hive-default.xml 