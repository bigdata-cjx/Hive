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
