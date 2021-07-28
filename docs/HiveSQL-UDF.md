# UDF编程
## 自定义UDF函数
继承 UDF 类，开发 evaluate方法。
```
pom.xml文件内容：
<properties>
  <hive.version>1.1.0</hive.version>
</properties>

<dependency>
  <groupId>org.apache.hive</groupId>
  <artifactId>hive-exec</artifactId>
  <version>${hive.version}</version>
</dependency>
```
## 打jar包
## 添加jar包
```
docker cp ./out/artifacts/Hive_jar/Hive.jar b4c3d4354fae:/opt/jars/
hive (default)> add jar /opt/jars/Hive.jar;
Added [/opt/jars/Hive.jar] to class path
Added resources: [/opt/jars/Hive.jar]
hive (default)> list jar;
/opt/jars/Hive.jar
```  
## 创建函数
```
hive (default)> create temporary function cjx_udf  as 'hiveudf.CjxUDF';
hive (default)> show functions;
```
## 使用自定义函数
```
hive (default)> select cjx_udf("spark");
OK
_c0
SPARK
```