# 添加Hive
## MySQL设置用户的链接
```
docker pull mysql:5.7
[root@bigdata-pro03 ~]# docker run --net bigdata --ip 172.168.0.3 --name bigdata-mysql57 -e MYSQL_ROOT_PASSWORD=123456 -d mysql:5.7
docker exec -it bigdata-mysql57 /bin/bash
mysql -uroot -p123456
```
## 下载hive-2.3.4
http://archive.apache.org/dist/hive/hive-2.3.4/

## 安装配置Hive
```
8088:yarn wenui端口
[root@bigdata-pro03 ~]# docker run -it --net bigdata --ip 172.168.0.4 --name hive dockerhubbigdata/bigdata:hadoop-alone /bin/bash
root@09dbdeaa9292:~# 
root@09dbdeaa9292:~# cd /opt/modules/

docker cp ./apache-hive-2.3.4-bin.tar.gz 09dbdeaa9292:/opt/modules/

root@09dbdeaa9292:/opt/modules# tar -zxf apache-hive-2.3.4-bin.tar.gz -C ./
root@09dbdeaa9292:/opt/modules# mv apache-hive-2.3.4-bin hive-2.3.4-bin
root@09dbdeaa9292:/opt/modules# rm apache-hive-2.3.4-bin.tar.gz

[root@bigdata-pro03 softwares]# docker cp ./hive-site.xml 09dbdeaa9292:/opt/modules/hive-2.3.4-bin/conf/
[root@bigdata-pro03 softwares]# docker cp ./hive-env.sh 09dbdeaa9292:/opt/modules/hive-2.3.4-bin/conf/
```
### Hive添加MySQL驱动包
```
[root@bigdata-pro03 softwares]# docker cp ./mysql-connector-java-5.1.27-bin.jar 09dbdeaa9292:/opt/modules/hive-2.3.4-bin/lib/
[kfk@bigdata-pro03 hive-0.13.1-bin]$ ll lib/ | grep mysql
-rw-rw-r--  1 kfk kfk   872303 Sep  6 19:29 mysql-connector-java-5.1.27-bin.jar
-rw-r--r--.  1 root root    872300 Jan 15 17:12 mysql-connector-java-5.1.27.jar
```
### 启动测试(需要先启动hadoop)
```
docker run -it -p 8088:8088 -p 50070:50070 -p 50075:50075 --net bigdata --ip 172.168.0.2 --name hadoop dockerhubbigdata/bigdata:hadoop-alone /bin/bash
service ssh start
/opt/modules/hadoop-2.7.0/sbin/start-dfs.sh
```
#### 数据准备(注意字段之间的分隔符，有些编辑器里会把制表符以四个空格的方式表示)
```
root@09dbdeaa9292:/opt/modules# apt-get install vim -y
root@09dbdeaa9292:/opt/modules# mkdir datas
root@09dbdeaa9292:/opt/modules/datas# vim test.txt
0001    hadoop
0002    zookeeeper
0003    hbase
0004    kafka
```
#### Hive测试
```
后台启动
root@d9bb2453ce32:/opt/modules/hive-2.3.4-bin# bin/hive --service metastore &
shell启动
root@d9bb2453ce32:/opt/modules/hive-2.3.4-bin# bin/hive
hive> show databases;
hive> use default;
hive (default)> create table test(id int,name string) ROW FORMAT delimited FIELDS TERMINATED BY '\t';
hive (default)> show tables;
hive (default)> desc test;
OK
col_name        data_type       comment
id                      int                                         
name                    string   
hive (default)> load data local inpath '/opt/modules/datas/test.txt' into table test;
hive (default)> select * from test;
OK
test.id test.name
1       hadoop
2       zookeeeper
3       hbase
4       kafka
Time taken: 0.315 seconds, Fetched: 4 row(s)
```
### 提交镜像
```
docker commit hive dockerhubbigdata/bigdata:hive
docker push dockerhubbigdata/bigdata:hive
```

# 镜像测试
```
启动hadoop
docker run -it -p 8088:8088 -p 50070:50070 -p 50075:50075 --net bigdata --ip 172.168.0.2 --name hadoop dockerhubbigdata/bigdata:hadoop-alone /bin/bash
service ssh start
/opt/modules/hadoop-2.7.0/sbin/start-dfs.sh

docker run --net bigdata --ip 172.168.0.3 --name bigdata-mysql57 -e MYSQL_ROOT_PASSWORD=123456 -d mysql:5.7

docker run -it --net bigdata --ip 172.168.0.4 --name hive dockerhubbigdata/bigdata:hive /bin/bash
/opt/modules/hive-2.3.4-bin/bin/hive
hive (default)> show databases;
OK
database_name
default
Time taken: 4.681 seconds, Fetched: 1 row(s)
hive (default)> use default;
OK
Time taken: 0.022 seconds
hive (default)> create table test(id int,name string) ROW FORMAT delimited FIELDS TERMINATED BY '\t';
OK
Time taken: 0.68 seconds
hive (default)> show tables;
OK
tab_name
test
Time taken: 0.02 seconds, Fetched: 1 row(s)
hive (default)> load data local inpath '/opt/modules/datas/test.txt' into table test;
Loading data to table default.test
OK
Time taken: 1.617 seconds
hive (default)> select * from test;
OK
test.id test.name
1       hadoop
2       zookeeeper
3       hbase
4       kafka

docker exec -it bigdata-mysql57 /bin/bash
mysql -uroot -p123456
mysql> select user,host from mysql.user;
+---------------+-----------+
| user          | host      |
+---------------+-----------+
| root          | %         |
| mysql.session | localhost |
| mysql.sys     | localhost |
| root          | localhost |
+---------------+-----------+
4 rows in set (0.00 sec)

mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| metastore          |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
5 rows in set (0.00 sec)

mysql> use metastore;
Reading table information for completion of table and column names
You can turn off this feature to get a quicker startup with -A

Database changed
mysql> show tables;
+---------------------------+
| Tables_in_metastore       |
+---------------------------+
| BUCKETING_COLS            |
| CDS                       |
| COLUMNS_V2                |
| DATABASE_PARAMS           |
| DBS                       |
| FUNCS                     |
| FUNC_RU                   |
| GLOBAL_PRIVS              |
| PARTITIONS                |
| PARTITION_KEYS            |
| PARTITION_KEY_VALS        |
| PARTITION_PARAMS          |
| PART_COL_STATS            |
| ROLES                     |
| SDS                       |
| SD_PARAMS                 |
| SEQUENCE_TABLE            |
| SERDES                    |
| SERDE_PARAMS              |
| SKEWED_COL_NAMES          |
| SKEWED_COL_VALUE_LOC_MAP  |
| SKEWED_STRING_LIST        |
| SKEWED_STRING_LIST_VALUES |
| SKEWED_VALUES             |
| SORT_COLS                 |
| TABLE_PARAMS              |
| TAB_COL_STATS             |
| TBLS                      |
| TBL_PRIVS                 |
| VERSION                   |
+---------------------------+
30 rows in set (0.00 sec)

mysql> select * from TBLS;
+--------+-------------+-------+------------------+-------+-----------+--------------------+-------+----------+---------------+--------------------+--------------------+
| TBL_ID | CREATE_TIME | DB_ID | LAST_ACCESS_TIME | OWNER | RETENTION | IS_REWRITE_ENABLED | SD_ID | TBL_NAME | TBL_TYPE      | VIEW_EXPANDED_TEXT | VIEW_ORIGINAL_TEXT |
+--------+-------------+-------+------------------+-------+-----------+--------------------+-------+----------+---------------+--------------------+--------------------+
|      1 |  1610739007 |     1 |                0 | root  |         0 |                    |     1 | test     | MANAGED_TABLE | NULL               | NULL               |
+--------+-------------+-------+------------------+-------+-----------+--------------------+-------+----------+---------------+--------------------+--------------------+
1 row in set (0.00 sec)
```