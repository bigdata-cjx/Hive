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
➜  /opt sudo mkdir track
[sudo] cjx 的密码： 
➜  /opt sudo chown -R cjx:cjx track/
➜  /opt cd track 
➜  track mkdir track_log
➜  20210801 pwd
/opt/track/track_log/20210801
➜  20210801 cp /opt/datas/2015082818 2021080100
➜  20210801 cp /opt/datas/2015082818 2021080101
➜  20210801 cp /opt/datas/2015082818 2021080102
➜  20210801 cp /opt/datas/2015082818 2021080103
➜  20210801 cd ..
➜  track_log tree -L 2
.
└── 20210801
    ├── 2021080100
    ├── 2021080101
    ├── 2021080102
    └── 2021080103
```
### load_track_log.sh 脚本内容
```
#!/bin/sh
. /etc/profile

##track log dir path
LOD_DIR=/opt/track/track_log

##hive home

HIVE_HOME=/opt/modules/hive-1.1.0-cdh5.5.0

yesterday=`date -d "1 day ago" +"%Y%m%d"`

cd $LOD_DIR

for line in `ls $yesterday`;
do
    date=${line:0:4}${line:4:2}${line:6:2}
    hour=${line:8:2}
    $HIVE_HOME/bin/hive -e "load data local inpath '$LOD_DIR/$yesterday/$line'
    overwrite into table track.track_log 
    partition(date='$date',hour='$hour')"
done
```
### daily_hour_visit.sh 脚本内容
```
#!/bin/sh
HIVE_HOME=/opt/modules/hive-1.1.0-cdh5.5.0
SQOOP_HOME=/opt/modules/sqoop-1.4.6-cdh5.5.0
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
### hive-visit.sql 内容
```
truncate table track.daily_hour_visit ;

insert into table  track.daily_hour_visit 
select  date,hour,count(url) pv, count(distinct guid) uv from track.track_log 
where date='${hiveconf:yesterday}' group by date,hour;
```
### export_visit.txt 内容
```
export
--connect
jdbc:mysql://bigdata-pro-m03.kfk.com:3306/db_track 
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
 
  <property>
    <name>hive.limit.optimize.enable</name>
    <value>false</value>
    <description>Whether to enable to optimization to trying a smaller subset of data for simple LIMIT first.</description>
  </property>


  实际应用中，会设置为
  hive.limit.optimize.enable = true;
