# Hive表数据的加载方式
## 加载本地数据文件到hive表中
load data local inpath '/opt/datas/order.txt' into table db_hive.order;
## 加载HDFS文件到hive表中
load data  inpath '/opt/datas/order.txt' into table db_hive.order;
## 加载数据覆盖表中已有的数据
load data  inpath '/opt/datas/order.txt' overwrite into table db_hive.order;
## 创建表时通过select加载数据
create table order as select * from nopartition;
## 创建表是，通过insert 加载数据
```
create table order like nopartition;
insert into order select * from nopartition;
```
## 创建表是，通过location指定的数据目录加载
1. 创建数据表目录
2. 将数据文件放到指定的目录
3. 创建表并location指定数据目录
# Hive结果数据的保存方式
## 结果数据保存到本地文件中
```
INSERT OVERWRITE LOCAL DIRECTORY '/opt/datas/kfk/'
SELECT * FROM order

INSERT OVERWRITE LOCAL DIRECTORY '/opt/datas/kfk/'
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
SELECT * FROM order
```
## 结果数据保存到hdfs
```
INSERT OVERWRITE  DIRECTORY '/user/kfk/datas/order/'
SELECT * FROM order;
```
## 管道符
```
bin/hive -e "select * from db_hive.order;"  >  /opt/datas/kfk/order.txt
```
# 数据备份
## export(导出到HDFS)
```
EXPORT TABLE db_hive.order 
TO '/user/kfk/datas/export/order'
```
## import：
```
import table order_imp from '/user/kfk/datas/export/order';

IMPORT  TABLE order_imp_1 
FROM '/user/kfk/datas/export/order'
LOCATION '/user/kfk/datas/imp/order';    --（location）指定数据表目录
```
# 常用的查询
1. 字段查询: select userid,username from order;
2.where查询: select * from order where price > 200;
3.limit查询: select * from order limit 2;
4.distinct: select distinct city from order;
5.max/min/count/sum: select max(price) from order;
6.group by / having: select sum(price) price,city from order group by city having price > 500;
## join
### 等值连接
```
SELECT * 
FROM customer t1, order t2
WHERE t1.userid = t2.userid ;
```
### 左连接
```
select  t1.username,t2.product_name  from customer t1 left join order t2 on t1.userid = t2.userid;
select  t2.username,t1.product_name  from order t1 left join customer t2 on t1.userid = t2.userid;
```
### 右连接
```
select  t1.username,t2.product_name  from customer t1 right join order t2 on t1.userid = t2.userid;
select  t2.username,t1.product_name  from order t1 right join customer t2 on t1.userid = t2.userid;
```
### 全连接
```
select  t2.username,t1.product_name  from order t1 full join customer t2 on t1.userid = t2.userid;
```
# 几种 by 的使用
set mapreduce.job.reduces=3
## order by
只有一个reduce ，全局排序。设置的reduce数量无效。
```
select * from order order by price desc
select * from order order by price asc
```
## sort by
对每一个reduce内部的数据进行排序，最后的全局结果集不排序。最终生成的文件数量等于设置的reduce数量。
```
insert overwrite local directory '/opt/datas/kfk/sort'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
select * from order sort by price desc;
```
## distribute by 
作用类似于partition,一般与 sort by 一起使用。最终生成的文件数量等于设置的reduce数量。
```
insert overwrite local directory '/opt/datas/kfk/sort'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
select * from order distribute by username sort by price desc;
```
## cluster by 
distribute by 和 sort by字段相同时，使用cluster by 代替
```
insert overwrite local directory '/opt/datas/kfk/sort'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
select * from order cluster by username;
```
