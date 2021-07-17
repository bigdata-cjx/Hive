# 开启Hive
```
root@b4c3d4354fae:/opt/modules/hive-2.3.4-bin# bin/hive
```
# 配置方式
1. MR程序
2. mapred-site.xml(hadoop-2.6.0/etc/hadoop/)
3. hive命令行

# Map端数据输出压缩
```
hive (default)> set hive.exec.compress.intermediate = true;
hive (default)> set mapreduce.map.output.compress = true;
hive (default)> set mapreduce.map.output.compress.codec = org.apache.hadoop.io.compress.SnappyCodec;
hive (default)> set mapreduce.map.output.compress.codec;
mapreduce.map.output.compress.codec=org.apache.hadoop.io.compress.SnappyCodec
hive (default)> select count(*) from u_data;
OK
_c0
100837
Time taken: 12.899 seconds, Fetched: 1 row(s)
```
# Reduce端数据输出压缩
```
hive (default)> set hive.exec.compress.output = true;
hive (default)> set mapreduce.output.fileoutputformat.compress = true;
hive (default)> set mapreduce.output.fileoutputformat.compress.codec = org.apache.hadoop.io.compress.SnappyCodec;
hive (default)> set mapreduce.output.fileoutputformat.compress.codec;
mapreduce.output.fileoutputformat.compress.codec=org.apache.hadoop.io.compress.SnappyCodec
hive (default)> select count(*) from u_data;
OK
_c0
Failed with exception java.io.IOException:java.lang.RuntimeException: native snappy library not available: SnappyCompressor has not been loaded.
Time taken: 11.47 seconds
```
## 因为Reduce过程会使用Hive配置的Hadoop文件下的包，所以需要将编译出来的snappy相关文件也发送到Hive配置的Hadoop文件下
```
➜  hadoop2.6.0编译snappy结果文件 docker cp native b4c3d4354fae:/opt/modules/
root@b4c3d4354fae:/opt/modules/hadoop-2.7.0/lib/native# mv /opt/modules/native/* ./

重新启动Hive
hive (default)> select count(*) from u_data;
OK
_c0
100837
Time taken: 11.971 seconds, Fetched: 1 row(s)
```