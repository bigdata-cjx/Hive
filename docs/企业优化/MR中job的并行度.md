在实际应用中，复杂的hive sql语句在执行的过程中会有多个job，如果说在这些个job中，没有相互依赖关系的job我们可以设定让他们并行执行
```
<property>
    <name>hive.exec.parallel</name>
    <value>false</value>
    <description>Whether to execute jobs in parallel</description>
</property>

<property>
    <name>hive.exec.parallel.thread.number</name>
    <value>8</value>
    <description>How many jobs at most can be executed in parallel</description>
</property>
```
# 测试
```
hive (default)> desc u_data;
OK
col_name	data_type	comment
userid              	int                 	                    
movieid             	int                 	                    
rating              	string              	                    
unixtime            	string              	                    
Time taken: 0.021 seconds, Fetched: 4 row(s)

hive (default)> select count(distinct userid) from u_data;
OK
_c0
610
Time taken: 11.441 seconds, Fetched: 1 row(s)

hive (default)> select count(distinct movieid) from u_data;
OK
_c0
9724
Time taken: 11.712 seconds, Fetched: 1 row(s)
```
## set hive.exec.parallel = false;
```
hive (default)> set hive.exec.parallel = false;
hive (default)> select count(distinct userid) from u_data
              > union all 
              > select count(distinct movieid) from u_data;

Query ID = root_20210720232201_51dace9c-3beb-4f9f-a918-c77ded81988f
Total jobs = 3
Launching Job 1 out of 3
Number of reduce tasks determined at compile time: 1
In order to change the average load for a reducer (in bytes):
  set hive.exec.reducers.bytes.per.reducer=<number>
In order to limit the maximum number of reducers:
  set hive.exec.reducers.max=<number>
In order to set a constant number of reducers:
  set mapreduce.job.reduces=<number>
Starting Job = job_1626817574182_0003, Tracking URL = http://bigdata:8088/proxy/application_1626817574182_0003/
Kill Command = /opt/modules/hadoop-2.7.0/bin/hadoop job  -kill job_1626817574182_0003
Hadoop job information for Stage-1: number of mappers: 1; number of reducers: 1
2021-07-20 23:22:06,496 Stage-1 map = 0%,  reduce = 0%
2021-07-20 23:22:09,576 Stage-1 map = 100%,  reduce = 0%, Cumulative CPU 1.24 sec
2021-07-20 23:22:13,656 Stage-1 map = 100%,  reduce = 100%, Cumulative CPU 2.17 sec
MapReduce Total cumulative CPU time: 2 seconds 170 msec
Ended Job = job_1626817574182_0003
Launching Job 2 out of 3
Number of reduce tasks determined at compile time: 1
In order to change the average load for a reducer (in bytes):
  set hive.exec.reducers.bytes.per.reducer=<number>
In order to limit the maximum number of reducers:
  set hive.exec.reducers.max=<number>
In order to set a constant number of reducers:
  set mapreduce.job.reduces=<number>
Starting Job = job_1626817574182_0004, Tracking URL = http://bigdata:8088/proxy/application_1626817574182_0004/
Kill Command = /opt/modules/hadoop-2.7.0/bin/hadoop job  -kill job_1626817574182_0004
Hadoop job information for Stage-3: number of mappers: 1; number of reducers: 1
2021-07-20 23:22:22,925 Stage-3 map = 0%,  reduce = 0%
2021-07-20 23:22:25,989 Stage-3 map = 100%,  reduce = 0%, Cumulative CPU 1.51 sec
2021-07-20 23:22:30,058 Stage-3 map = 100%,  reduce = 100%, Cumulative CPU 2.94 sec
MapReduce Total cumulative CPU time: 2 seconds 940 msec
Ended Job = job_1626817574182_0004
Launching Job 3 out of 3
Number of reduce tasks is set to 0 since there's no reduce operator
Starting Job = job_1626817574182_0005, Tracking URL = http://bigdata:8088/proxy/application_1626817574182_0005/
Kill Command = /opt/modules/hadoop-2.7.0/bin/hadoop job  -kill job_1626817574182_0005
Hadoop job information for Stage-2: number of mappers: 2; number of reducers: 0
2021-07-20 23:22:38,358 Stage-2 map = 0%,  reduce = 0%
2021-07-20 23:22:41,425 Stage-2 map = 100%,  reduce = 0%, Cumulative CPU 1.45 sec
MapReduce Total cumulative CPU time: 1 seconds 450 msec
Ended Job = job_1626817574182_0005
MapReduce Jobs Launched: 
Stage-Stage-1: Map: 1  Reduce: 1   Cumulative CPU: 2.17 sec   HDFS Read: 2491190 HDFS Write: 116 SUCCESS
Stage-Stage-3: Map: 1  Reduce: 1   Cumulative CPU: 2.94 sec   HDFS Read: 2491195 HDFS Write: 116 SUCCESS
Stage-Stage-2: Map: 2   Cumulative CPU: 1.45 sec   HDFS Read: 7138 HDFS Write: 207 SUCCESS
Total MapReduce CPU Time Spent: 6 seconds 560 msec
OK
_u1._c0
610
9724
Time taken: 41.275 seconds, Fetched: 2 row(s)
```
## set hive.exec.parallel = true;
hive优化器会自动给出合适的并行度
```
hive (default)> set hive.exec.parallel = true;
hive (default)> select count(distinct userid) from u_data
              > union all 
              > select count(distinct movieid) from u_data;

Query ID = root_20210720232538_643b9408-4388-4b3d-8fe8-af116f71972a
Total jobs = 3
Launching Job 1 out of 3
Launching Job 2 out of 3
Number of reduce tasks determined at compile time: 1
In order to change the average load for a reducer (in bytes):
  set hive.exec.reducers.bytes.per.reducer=<number>
In order to limit the maximum number of reducers:
  set hive.exec.reducers.max=<number>
In order to set a constant number of reducers:
  set mapreduce.job.reduces=<number>
Number of reduce tasks determined at compile time: 1
In order to change the average load for a reducer (in bytes):
  set hive.exec.reducers.bytes.per.reducer=<number>
In order to limit the maximum number of reducers:
  set hive.exec.reducers.max=<number>
In order to set a constant number of reducers:
  set mapreduce.job.reduces=<number>
Starting Job = job_1626817574182_0007, Tracking URL = http://bigdata:8088/proxy/application_1626817574182_0007/
Kill Command = /opt/modules/hadoop-2.7.0/bin/hadoop job  -kill job_1626817574182_0007
Starting Job = job_1626817574182_0008, Tracking URL = http://bigdata:8088/proxy/application_1626817574182_0008/
Kill Command = /opt/modules/hadoop-2.7.0/bin/hadoop job  -kill job_1626817574182_0008
Hadoop job information for Stage-1: number of mappers: 1; number of reducers: 1
2021-07-20 23:25:41,315 Stage-1 map = 0%,  reduce = 0%
2021-07-20 23:25:44,374 Stage-1 map = 100%,  reduce = 0%, Cumulative CPU 1.21 sec
2021-07-20 23:25:48,448 Stage-1 map = 100%,  reduce = 100%, Cumulative CPU 2.16 sec
MapReduce Total cumulative CPU time: 2 seconds 160 msec
Ended Job = job_1626817574182_0007
Hadoop job information for Stage-3: number of mappers: 1; number of reducers: 1
2021-07-20 23:25:57,330 Stage-3 map = 0%,  reduce = 0%
2021-07-20 23:26:00,388 Stage-3 map = 100%,  reduce = 0%, Cumulative CPU 1.58 sec
2021-07-20 23:26:04,487 Stage-3 map = 100%,  reduce = 100%, Cumulative CPU 3.12 sec
MapReduce Total cumulative CPU time: 3 seconds 120 msec
Ended Job = job_1626817574182_0008
Launching Job 3 out of 3
Number of reduce tasks is set to 0 since there's no reduce operator
Starting Job = job_1626817574182_0009, Tracking URL = http://bigdata:8088/proxy/application_1626817574182_0009/
Kill Command = /opt/modules/hadoop-2.7.0/bin/hadoop job  -kill job_1626817574182_0009
Hadoop job information for Stage-2: number of mappers: 2; number of reducers: 0
2021-07-20 23:26:13,250 Stage-2 map = 0%,  reduce = 0%
2021-07-20 23:26:16,346 Stage-2 map = 100%,  reduce = 0%, Cumulative CPU 1.5 sec
MapReduce Total cumulative CPU time: 1 seconds 500 msec
Ended Job = job_1626817574182_0009
MapReduce Jobs Launched: 
Stage-Stage-1: Map: 1  Reduce: 1   Cumulative CPU: 2.16 sec   HDFS Read: 2491235 HDFS Write: 116 SUCCESS
Stage-Stage-3: Map: 1  Reduce: 1   Cumulative CPU: 3.12 sec   HDFS Read: 2491240 HDFS Write: 116 SUCCESS
Stage-Stage-2: Map: 2   Cumulative CPU: 1.5 sec   HDFS Read: 7138 HDFS Write: 207 SUCCESS
Total MapReduce CPU Time Spent: 6 seconds 780 msec
OK
_u1._c0
610
9724
Time taken: 40.116 seconds, Fetched: 2 row(s)
```