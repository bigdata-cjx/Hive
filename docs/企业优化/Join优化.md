# map join
场景: 小表与大表的join，将小表数据加载到内存中。然后在map端完成与大表数据的join
```
setup()
map()
cleanup()
```
# reduce join/shuffle join/common join
场景：两个表的数据量都比较大的时候，我们会采用shuffle join。

两个表的数据作为map端的输入，在reduce端进行相同key的value值合并。

customer表和order表，根据两个表的连接字段 user_id 进行合并<user_id,List(customer + order表)>
# SMB join (Sort-Merge-Bucket)
https://cwiki.apache.org/confluence/display/Hive/LanguageManual+JoinOptimization#LanguageManualJoinOptimization-AutoConversiontoSMBMapJoin
```
customer表       userid字段
(bucket -1 ) 0001 ---- 0009   
(bucket -2 ) 0010 -----0020
(bucket -3 ) 0021 -----0030   

order表        userid字段
(bucket -1 ) 0001 ---- 0009   
(bucket -2 ) 0010 -----0020
(bucket -3 ) 0021 -----0030   

set hive.auto.convert.sortmerge.join=true;
set hive.optimize.bucketmapjoin = true;
set hive.optimize.bucketmapjoin.sortedmerge = true;
```