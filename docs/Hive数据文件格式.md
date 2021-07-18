# 存储在hive表的数据对应HDFS上的文件格式
RCFile、ORC File、Parquet都是列式存储结构。
ORC File、Parquet，这两个格式是企业中常用的(hive,impala,spark)
## Text File
文件存储。常用来做测试用。
## SequenceFile <key,value> 
二进制存储。一般用来存储小文件，key作为文件名，value对应文件内容
## RCFile  
## Avro Files
序列化框架。
## ORC Files
### 创建ORC格式数据表的方式
1. CREATE TABLE ... STORED AS ORC
2. ALTER TABLE ... [PARTITION partition_spec] SET FILEFORMAT ORC
3. SET hive.default.fileformat=Orc
```
create table Addresses (
  name string,
  street string,
  city string,
  state string,
  zip int
) stored as orc tblproperties ("orc.compress"="NONE");
```
## Parquet
## Custom INPUTFORMAT and OUTPUTFORMAT
自定义
# 测试不同的文件格式
## Text File
```
hive (default)> CREATE  TABLE movie (
              >   movie_id string,
              >   movie_name string,
              >   movie_type string
              >   )
              > ROW FORMAT DELIMITED
              > FIELDS TERMINATED BY ','
              > STORED AS TEXTFILE;

hive (default)> load data local inpath '/opt/datas/ml-latest-small/movies.csv' into table movie;
hive (default)> select * from movie limit 3;
movie.movie_id	movie.movie_name	movie.movie_type
movieId	title	genres
1	Toy Story (1995)	Adventure|Animation|Children|Comedy|Fantasy
2	Jumanji (1995)	Adventure|Children|Fantasy
Time taken: 0.06 seconds, Fetched: 3 row(s)
```
### 存储容量
/user/hive/warehouse/movie Size: 482.84 KB
### 计算速度
```
hive (default)> select count(1) from movie;
OK
_c0
9743
Time taken: 10.444 seconds, Fetched: 1 row(s)
```
## ORC Files
```
hive (default)> CREATE  TABLE movie_orc (
              >   movie_id string,
              >   movie_name string,
              >   movie_type string
              >   )
              > ROW FORMAT DELIMITED
              > FIELDS TERMINATED BY ','
              > STORED AS ORC;

hive (default)> insert into movie_orc select * from movie;
hive (default)> select * from movie_orc limit 3;
movie_orc.movie_id	movie_orc.movie_name	movie_orc.movie_type
movieId	title	genres
1	Toy Story (1995)	Adventure|Animation|Children|Comedy|Fantasy
2	Jumanji (1995)	Adventure|Children|Fantasy
Time taken: 0.041 seconds, Fetched: 3 row(s)
```
### 存储容量
/user/hive/warehouse/movie_orc Size: 154.73 KB
### 计算速度
```
hive (default)> select count(1) from movie_orc;
没有走MR
OK
_c0
9743
Time taken: 0.041 seconds, Fetched: 1 row(s)
```
## Parquet
```
hive (default)> CREATE  TABLE movie_parquet (
              >   movie_id string,
              >   movie_name string,
              >   movie_type string
              >   )
              > ROW FORMAT DELIMITED
              > FIELDS TERMINATED BY ','
              > STORED AS PARQUET;

hive (default)> insert into movie_parquet select * from movie;

hive (default)> select * from movie_parquet limit 3;
OK
movie_parquet.movie_id	movie_parquet.movie_name	movie_parquet.movie_type
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
movieId	title	genres
1	Toy Story (1995)	Adventure|Animation|Children|Comedy|Fantasy
2	Jumanji (1995)	Adventure|Children|Fantasy
Time taken: 0.037 seconds, Fetched: 3 row(s)
```
### 存储容量
/user/hive/warehouse/movie_parquet Size: 391.83 KB	
### 计算速度
```
hive (default)> select count(1) from movie_parquet;
没有走MR
OK
_c0
9743
Time taken: 0.04 seconds, Fetched: 1 row(s)
```
# 使用 snappy 压缩
## orc + snappy
```
hive (default)> CREATE  TABLE movie_orc_snappy (
              >   movie_id string,
              >   movie_name string,
              >   movie_type string
              >   )
              > ROW FORMAT DELIMITED
              > FIELDS TERMINATED BY ','
              > STORED AS ORC tblproperties ("orc.compress"="SNAPPY");

hive (default)> desc formatted movie_orc_snappy;
hive (default)> insert into movie_orc_snappy select * from movie;
Time taken: 7.474 seconds
hive (default)> select * from movie_orc_snappy limit 3;
OK
movie_orc_snappy.movie_id	movie_orc_snappy.movie_name	movie_orc_snappy.movie_type
movieId	title	genres
1	Toy Story (1995)	Adventure|Animation|Children|Comedy|Fantasy
2	Jumanji (1995)	Adventure|Children|Fantasy
Time taken: 0.037 seconds, Fetched: 3 row(s)
```
### 存储容量
/user/hive/warehouse/movie_orc_snappy Size: 224.97 KB	
### 计算速度
```
hive (default)> select count(1) from movie_orc_snappy;
没有走MR
OK
_c0
9743
Time taken: 0.041 seconds, Fetched: 1 row(s)
```
## parquet + snappy
```
hive (default)> 
hive (default)> CREATE  TABLE movie_parquet_snappy (
              >   movie_id string,
              >   movie_name string,
              >   movie_type string
              >   )
              > ROW FORMAT DELIMITED
              > FIELDS TERMINATED BY ','
              > STORED AS PARQUET;

hive (default)> desc formatted movie_parquet_snappy;
hive (default)> insert into movie_parquet_snappy select * from movie;
Time taken: 6.538 seconds
hive (default)> select * from movie_parquet_snappy limit 3;
OK
movie_orc_snappy.movie_id	movie_orc_snappy.movie_name	movie_orc_snappy.movie_type
movieId	title	genres
1	Toy Story (1995)	Adventure|Animation|Children|Comedy|Fantasy
2	Jumanji (1995)	Adventure|Children|Fantasy
Time taken: 0.037 seconds, Fetched: 3 row(s)
```
### 存储容量
/user/hive/warehouse/movie_parquet_snappy Size: 250.82 KB	
### 计算速度
```
hive (default)> select count(1) from movie_parquet_snappy;
没有走MR
OK
_c0
9743
Time taken: 0.037 seconds, Fetched: 1 row(s)
```
# 总结
在实际项目开发中，hive表的数据
- 存储格式: orc/parquet
- 数据压缩：snappy