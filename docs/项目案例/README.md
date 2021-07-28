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
## 数据处理二

# 虚拟列
```
select INPUT__FILE__NAME, userid, BLOCK__OFFSET__INSIDE__FILE from u_data_new;

INPUT__FILE__NAME: 表数据路径
BLOCK__OFFSET__INSIDE__FILE: 该行值在该文件中的偏移量
```