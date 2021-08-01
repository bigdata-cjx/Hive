# 网站日志分析 hive + sqoop + mysql 

# 案例第一阶段
## 需求分析
pv, uv 
## 实施分案
1. 创建hive分区表(date,hour)
2. 加载数据
3. 数据分析生产结果表
4. 导出结果表数据到MySQL
## 实施过程
### 创建表
```
hive (default)> create database track;

create table track.track_log (
    id               string,
    url              string,
    referer          string,
    keyword          string,
    type             string,
    guid             string,
    pageId           string,
    moduleId         string,
    linkId           string,
    attachedInfo     string,
    sessionId        string,
    trackerU         string,
    trackerType      string,
    ip               string,
    trackerSrc       string,
    cookie           string,
    orderCode        string,
    trackTime        string,
    endUserId        string,
    firstLink        string,
    sessionViewNo    string,
    productId        string,
    curMerchantId    string,
    provinceId       string,
    cityId           string,
    fee              string,
    edmActivity      string,
    edmEmail         string,
    edmJobId         string,
    ieVersion        string,
    platform         string,
    internalKeyword  string,
    resultSum        string,
    currentPage      string,
    linkPosition     string,
    buttonPosition   string
) 
PARTITIONED BY(`date` string, hour string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t';
```
### 加载数据
```
truncate table track_log;

load data local inpath '/opt/datas/2015082818' overwrite into table track.track_log 
partition(`date` = '20150828',hour = '18');

load data local inpath '/opt/datas/2015082819' overwrite into table track.track_log 
partition(date='20150828',hour='19');

select count(1) from track.track_log;
```
### 数据分析(pv + uv)
```
create table track.daily_hour_visit (
    `date` string,
    hour string,
    pv string,
    uv string ) 
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' ;

insert into table track.daily_hour_visit 
select `date`,hour,count(url) pv, count(distinct guid) uv from track.track_log 
where `date`='20150828' group by `date`, hour;

select * from track.daily_hour_visit;
```
### 导出数据到mysql
```
mysql> create database db_track;
mysql> use db_track;

create table daily_hour_visit(
  date varchar(30) not null,
  hour varchar(30) not null,
  pv varchar(30) not null,
  uv varchar(30) not null,
  primary key(date, hour)
);

bin/sqoop export \
--connect jdbc:mysql://localhost:3306/db_track \
--username root \
--password 123456 \
--num-mappers 1  \
--table daily_hour_visit \
--input-fields-terminated-by '\t' \
--export-dir /user/hive/warehouse/track.db/daily_hour_visit

mysql> select * from daily_hour_visit;
```
# 案例第二阶段
网站日志数据每天一份, 数据会放到约定的一个目录(/opt/datas/)
```
20150828(目录)
     2015082800(数据)
     2015082801(数据)
     2015082802(数据)
     2015082803(数据)
     2015082804(数据)
     2015082805(数据)
     2015082806(数据)
     ..........
20150829(目录)
     2015082800(数据)
     2015082801(数据)
     2015082802(数据)
     2015082803(数据)
     2015082804(数据)
     2015082805(数据)
     2015082806(数据)
     ..........
```
实际应用，要考虑的
- 通过脚本自动进行数据加载(load)
- 通过脚本进行数据分析并自动导出结果(process + export)
## 需求
编写脚本实现自动调度运行hive分析和sqoop导出结果集（pv + uv）
## 实施方案 
1. 创建约定的数据目录
2. 创建运行脚本的目录
3. 编写数据自动加载脚本
4. 编写自动调度运行Hive和sqoop的脚本
## 实施过程
### 数据、目录准备
```
drop table track_log;
create table track.track_log (
    id               string,
    url              string,
    referer          string,
    keyword          string,
    type             string,
    guid             string,
    pageId           string,
    moduleId         string,
    linkId           string,
    attachedInfo     string,
    sessionId        string,
    trackerU         string,
    trackerType      string,
    ip               string,
    trackerSrc       string,
    cookie           string,
    orderCode        string,
    trackTime        string,
    endUserId        string,
    firstLink        string,
    sessionViewNo    string,
    productId        string,
    curMerchantId    string,
    provinceId       string,
    cityId           string,
    fee              string,
    edmActivity      string,
    edmEmail         string,
    edmJobId         string,
    ieVersion        string,
    platform         string,
    internalKeyword  string,
    resultSum        string,
    currentPage      string,
    linkPosition     string,
    buttonPosition   string
) 
PARTITIONED BY(day string, hour string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t';

➜  /opt sudo mkdir track
[sudo] cjx 的密码： 
➜  /opt sudo chown -R cjx:cjx track/
➜  /opt cd track 
➜  track mkdir track_log
➜  20210731 pwd
/opt/track/track_log/20210731
➜  20210801 cp /opt/datas/2015082818 2021073100
➜  20210801 cp /opt/datas/2015082818 2021073101
➜  20210801 cp /opt/datas/2015082818 2021073102
➜  20210801 cp /opt/datas/2015082818 2021073103
➜  20210801 cd ..
➜  track_log tree -L 2
.
└── 20210731
    ├── 2021073100
    ├── 2021073101
    ├── 2021073102
    └── 2021073103

hive (track)> truncate table track_log;
```
### 数据加载
目录、文件
```
➜  track pwd
/opt/track
➜  track touch load_track_log.sh
➜  track chmod +x load_track_log.sh
➜  track vim load_track_log.sh
```
load_track_log.sh 脚本内容
```
#!/bin/bash

. /etc/profile

##track log dir path
LOG_DIR=/opt/track/track_log

##hive home
HIVE_HOME=/bigdata/modules/apache-hive-2.3.4-bin

yesterday=`date -d "1 day ago" +"%Y%m%d"`

cd $LOG_DIR

for line in `ls $yesterday`;
do
	date=${line:0:4}${line:4:2}${line:6:2}
 	hour=${line:8:2}
 	echo $date $hour
	sql="load data local inpath '$LOG_DIR/$yesterday/$line' overwrite into table track.track_log partition(day='$date',hour='$hour')"
	echo $sql
	$HIVE_HOME/bin/hive -e "$sql"
done
```
查看结果
```
select count(1) from track_log;
```
### 数据分析&数据导出
hive-visit.sql 内容
```
➜  track vim hive-visit.sql

truncate table track.daily_hour_visit ;

insert into table track.daily_hour_visit 
select day,hour,count(url) pv, count(distinct guid) uv from track.track_log 
where day='${hiveconf:yesterday}' group by day,hour;
```
export_visit.txt 内容
```
➜  track vim export_visit.txt

export
--connect
jdbc:mysql://localhost:3306/db_track 
--username
root
--password
123456
--table
daily_hour_visit
--num-mappers 
1
--export-dir
/user/hive/warehouse/track.db/daily_hour_visit
--fields-terminated-by
'\t'
```
daily_hour_visit.sh 脚本内容
```
➜  track vim daily_hour_visit.sh
➜  track chmod +x daily_hour_visit.sh 

#!/bin/bash
HIVE_HOME=/bigdata/modules/apache-hive-2.3.4-bin
SQOOP_HOME=/bigdata/modules/sqoop-1.4.7.bin__hadoop-2.6.0
SCRIPT_HOME=/opt/track

yesterday=`date -d "1 day ago" +"%Y%m%d"`

echo "start ........"

echo "============== step 1 :  data process start ============"
$HIVE_HOME/bin/hive -hiveconf yesterday=$yesterday -f $SCRIPT_HOME/hive-visit.sql
echo "============== step 1 :  data process complete! ============"

echo "============== step 2 :  export mysql start ============"
$SQOOP_HOME/bin/sqoop --options-file $SCRIPT_HOME/export_visit.txt
echo "============== step 2 :  export mysql complete! ============"

echo " ******** end ******* "
```
#### 测试
```
➜  track ./daily_hour_visit.sh

mysql> select * from daily_hour_visit;
+----------+------+-------+-------+
| date     | hour | pv    | uv    |
+----------+------+-------+-------+
| 20150828 | 18   | 64972 | 23938 |
| 20210731 | 00   | 64972 | 23938 |
| 20210731 | 01   | 64972 | 23938 |
| 20210731 | 02   | 64972 | 23938 |
| 20210731 | 03   | 64972 | 23938 |
+----------+------+-------+-------+
```
# 优化
## limit优化
一般的limit是把数据文件全部加载完，再取前n条。优化后就是：只取文件的前n条。
```
<property>
<name>hive.limit.optimize.enable</name>
<value>false</value>
<description>Whether to enable to optimization to trying a smaller subset of data for simple LIMIT first.</description>
</property>

实际应用中，会设置为
set hive.limit.optimize.enable = true;
```
### 测试
```
hive (track)> set hive.limit.optimize.enable;
hive.limit.optimize.enable=false

hive (track)> select url,keyword from track_log limit 3;
OK
url	keyword
http://www.yhd.com/?union_ref=7&cp=0	
http://my.yhd.com/order/finishOrder.do?orderCode=5435446505152	
http://list.yhd.com/p/c5072-b-a-s1-v0-p1-price-d0-pid-pt1086211-pl1171565-m0-k?tp=44.1086211.0.0.0.Kxnn54p-11-FFJKr	
Time taken: 0.054 seconds, Fetched: 3 row(s)

hive (track)> set hive.limit.optimize.enable=true;
hive (track)> select url,keyword from track_log limit 3;
OK
url	keyword
http://www.yhd.com/?union_ref=7&cp=0	
http://my.yhd.com/order/finishOrder.do?orderCode=5435446505152	
http://list.yhd.com/p/c5072-b-a-s1-v0-p1-price-d0-pid-pt1086211-pl1171565-m0-k?tp=44.1086211.0.0.0.Kxnn54p-11-FFJKr	
Time taken: 0.043 seconds, Fetched: 3 row(s)
```