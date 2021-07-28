# 加载hive表之前，数据进行预处理
1. MR程序处理
2. 正则表达式(企业推荐）
3. python脚本
# 简单的用例
## 使用Python进行数据预处理
创建Hive表
```
CREATE TABLE u_data (
  userid INT,
  movieid INT,
  rating INT,
  unixtime STRING)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE;
```
数据源：https://grouplens.org/datasets/movielens/
```
wget http://files.grouplens.org/datasets/movielens/ml-100k.zip
unzip ml-100k.zip

LOAD DATA LOCAL INPATH '/opt/datas/ml-100k/u.data'
OVERWRITE INTO TABLE u_data;

SELECT COUNT(*) FROM u_data;
```
编写Python2程序
```
vim weekday_mapper.py

import sys
import datetime

for line in sys.stdin:
  line = line.strip()
  userid, movieid, rating, unixtime = line.split('\t')
  weekday = datetime.datetime.fromtimestamp(float(unixtime)).isoweekday()
  print '\t'.join([userid, movieid, rating, str(weekday)])
```
编写Python3程序
```
vim weekday_mapper.py

import sys
import datetime

for line in sys.stdin:
  line = line.strip()
  userid, movieid, rating, unixtime = line.split('\t')
  weekday = datetime.datetime.fromtimestamp(float(unixtime)).isoweekday()
  list = [userid, movieid, rating, str(weekday)]
  print('\t'.join('%s' %id for id in list))
```
使用Python程序处理数据
```
CREATE TABLE u_data_new (
  userid INT,
  movieid INT,
  rating INT,
  weekday INT)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t';

hive (default)> add FILE /opt/datas/weekday_mapper.py;

INSERT OVERWRITE TABLE u_data_new
SELECT
  TRANSFORM (userid, movieid, rating, unixtime)
  USING 'python weekday_mapper.py'
  AS (userid, movieid, rating, weekday)
FROM u_data;

SELECT COUNT(*) FROM u_data_new;

SELECT weekday, COUNT(*)
FROM u_data_new
GROUP BY weekday;
```
## 使用正则进行数据预处理
1. 表拆分，源数据不变，创建对应业务需求的字表
2. 基于子表的基础之上
    1. 数据文件存储格式:orc/parquet
    2. 数据文件压缩：snappy
    3. map output：中间结果数据压缩snappy
    4. 外部表
    5. 分区表
    6. UDF数据处理
    
创建Hive表
```
CREATE TABLE apachelog (
  host STRING,
  identity STRING,
  username STRING,
  time STRING,
  request STRING,
  status STRING,
  size STRING,
  referer STRING,
  agent STRING)
ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.RegexSerDe'
WITH SERDEPROPERTIES (
  "input.regex" = "([^ ]*) (-|[^ ]*) (-|[^ ]*) (-|\\[[^\\]]*\\]) (\"[^\"]*\") ([0-9]*) ([0-9]*) (\"[^\"]*\") (\"[^\"]*\")?"
)
STORED AS TEXTFILE;
```
```
load data local inpath '/opt/datas/access.log' into table apachelog;

SELECT COUNT(*) FROM apachelog;
```
创建业务子表
```
CREATE TABLE apache_log_comm (
  host STRING,
  time STRING,
  status STRING,
  referer STRING)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS ORC tblproperties ("orc.compress"="SNAPPY");

insert into table apache_log_comm 
select host,time,status,referer from apachelog;
```
编写 Java 程序
- DateTransform
- RemoveQuotes
- RemoveSquare

添加 自定义函数(Jar 包)
```
add jar /opt/jars/cjxhive.jar;
hive (default)> list jars;
/opt/jars/cjxhive.jar

create temporary function cjx_removeSquare as 'hiveudf.RemoveSquare';
create temporary function cjx_removeQuotes as 'hiveudf.RemoveQuotes';
create temporary function cjx_dateTransform as 'hiveudf.DateTransform';

hive (default)> show functions like 'cjx*';
OK
tab_name
cjx_datetransform
cjx_removequotes
cjx_removesquare
```
创建业务表
```
CREATE TABLE apache_log_opt
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS ORC tblproperties ("orc.compress"="SNAPPY")
AS select host,cjx_datetransform(cjx_removesquare(time)) time,status,cjx_removequotes(referer) referer from apache_log_comm;

select referer,count from (select referer,count(1) count from apache_log_opt group by referer) a order by count desc limit 5;
```
# 虚拟列
```
select INPUT__FILE__NAME, userid, BLOCK__OFFSET__INSIDE__FILE from u_data_new;

INPUT__FILE__NAME: 表数据路径
BLOCK__OFFSET__INSIDE__FILE: 该行值在该文件中的偏移量
```