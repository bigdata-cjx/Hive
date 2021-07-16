# 参考
```
https://blog.csdn.net/guluxiaogong/article/details/51754522
https://blog.csdn.net/yz972641975/article/details/98405720
```
# 添加必须文件
```
➜  modules scp apache-maven-3.3.9-bin.tar.gz root@bigdata-pro02:/opt/modules/
➜  modules scp jdk-7u79-linux-x64.gz root@bigdata-pro02:/opt/modules/
➜  modules scp protobuf-2.5.0.tar root@bigdata-pro02:/opt/modules/
➜  modules scp snappy-1.1.3.tar root@bigdata-pro02:/opt/modules/
➜  modules scp hadoop-2.6.0-src.tar.gz root@bigdata-pro02:/opt/modules/
➜  modules scp findbugs-3.0.1.tar.gz root@bigdata-pro02:/opt/modules/
➜  modules scp apache-ant-1.9.16-bin.tar.gz root@bigdata-pro02:/opt/modules/

tar -xf hadoop-2.6.0-src.tar.gz -C ./
tar -xf apache-maven-3.3.9-bin.tar.gz -C ./
tar -xf jdk-7u79-linux-x64.gz -C ./
tar -xf protobuf-2.5.0.tar -C ./
tar -xf snappy-1.1.3.tar -C ./
tar -xf findbugs-3.0.1.tar.gz -C ./
tar -xf apache-ant-1.9.16-bin.tar.gz -C ./

rm -f ./*.gz
rm -f ./*.tar

vi /etc/profile
export JAVA_HOME=/opt/modules/jdk1.7.0_79
export MAVEN_HOME=/opt/modules/apache-maven-3.3.9
export FINDBUGS_HOME=/opt/modules/findbugs-3.0.1
export ANT_HOME=/opt/modules/apache-ant-1.10.11
export PATH=$JAVA_HOME/bin:$MAVEN_HOME/bin:$FINDBUGS_HOME/bin:$ANT_HOME/bin:$PATH

source /etc/profile
```
## Java
```
java -version
java version "1.7.0_79"
Java(TM) SE Runtime Environment (build 1.7.0_79-b15)
Java HotSpot(TM) 64-Bit Server VM (build 24.79-b02, mixed mode)
```
## Maven
```
mvn -version
Apache Maven 3.3.9 (bb52d8502b132ec0a5a3f4c09453c07478323dc5; 2015-11-10T16:41:47+00:00)
Maven home: /opt/modules/apache-maven-3.3.9
Java version: 1.7.0_79, vendor: Oracle Corporation
Java home: /opt/modules/jdk1.7.0_79/jre
Default locale: en_US, platform encoding: ANSI_X3.4-1968
OS name: "linux", version: "5.4.0-77-generic", arch: "amd64", family: "unix"

[root@bigdata-pro02 apache-maven-3.3.9]# vim conf/settings.xml 
<mirror>
  <id>alimaven</id>
  <name>aliyun maven</name>
  <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
  <mirrorOf>central</mirrorOf>
</mirror>
```
## ProtoBuf
```
[root@bigdata-pro02 protobuf-2.5.0]# yum install autoconf automake libtool make cmake g++ unzip glibc-headers gcc gcc-c++ openssl-devel ncurses-devel
[root@bigdata-pro02 protobuf-2.5.0]# ./configure
[root@bigdata-pro02 protobuf-2.5.0]# make
[root@bigdata-pro02 protobuf-2.5.0]# make install
```
## Snappy
```
[root@bigdata-pro02 snappy-1.1.3]# ./configure
[root@bigdata-pro02 snappy-1.1.3]# make
[root@bigdata-pro02 snappy-1.1.3]# make install
```
## 编译 hadoop-2.6.0
```
不要用1.10版本，否则会报错：Unsupported major.minor version 52.0（1.10是jdk8的，低版本不支持高版本）
[root@bigdata-pro02 modules]# ant -version
Apache Ant(TM) version 1.9.16 compiled on July 10 2021

findbugs -version
3.0.1

mvn clean package -Pdist,native -DskipTests -Dtar -Dbundle.snappy
mvn package -DskipTests -Pdist,native –Dtar
[root@bigdata-pro02 hadoop-2.6.0-src]# mvn clean package -Pdist,native -DskipTests -Dtar -Dbundle.snappy -Dsnappy.lib=/usr/local/lib 

[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO] 
[INFO] Apache Hadoop Main ................................. SUCCESS [  1.113 s]
[INFO] Apache Hadoop Project POM .......................... SUCCESS [  0.981 s]
[INFO] Apache Hadoop Annotations .......................... SUCCESS [  3.334 s]
[INFO] Apache Hadoop Assemblies ........................... SUCCESS [  0.228 s]
[INFO] Apache Hadoop Project Dist POM ..................... SUCCESS [  2.003 s]
[INFO] Apache Hadoop Maven Plugins ........................ SUCCESS [  2.978 s]
[INFO] Apache Hadoop MiniKDC .............................. SUCCESS [  2.644 s]
[INFO] Apache Hadoop Auth ................................. SUCCESS [  4.138 s]
[INFO] Apache Hadoop Auth Examples ........................ SUCCESS [  3.089 s]
[INFO] Apache Hadoop Common ............................... SUCCESS [02:15 min]
[INFO] Apache Hadoop NFS .................................. SUCCESS [  5.242 s]
[INFO] Apache Hadoop KMS .................................. SUCCESS [ 23.159 s]
[INFO] Apache Hadoop Common Project ....................... SUCCESS [  0.038 s]
[INFO] Apache Hadoop HDFS ................................. SUCCESS [03:20 min]
[INFO] Apache Hadoop HttpFS ............................... SUCCESS [ 25.350 s]
[INFO] Apache Hadoop HDFS BookKeeper Journal .............. SUCCESS [ 22.557 s]
[INFO] Apache Hadoop HDFS-NFS ............................. SUCCESS [  3.393 s]
[INFO] Apache Hadoop HDFS Project ......................... SUCCESS [  0.039 s]
[INFO] hadoop-yarn ........................................ SUCCESS [  0.036 s]
[INFO] hadoop-yarn-api .................................... SUCCESS [01:29 min]
[INFO] hadoop-yarn-common ................................. SUCCESS [ 42.419 s]
[INFO] hadoop-yarn-server ................................. SUCCESS [  0.038 s]
[INFO] hadoop-yarn-server-common .......................... SUCCESS [ 15.228 s]
[INFO] hadoop-yarn-server-nodemanager ..................... SUCCESS [ 47.343 s]
[INFO] hadoop-yarn-server-web-proxy ....................... SUCCESS [  2.577 s]
[INFO] hadoop-yarn-server-applicationhistoryservice ....... SUCCESS [  4.929 s]
[INFO] hadoop-yarn-server-resourcemanager ................. SUCCESS [ 15.652 s]
[INFO] hadoop-yarn-server-tests ........................... SUCCESS [  5.281 s]
[INFO] hadoop-yarn-client ................................. SUCCESS [  5.956 s]
[INFO] hadoop-yarn-applications ........................... SUCCESS [  0.036 s]
[INFO] hadoop-yarn-applications-distributedshell .......... SUCCESS [  2.250 s]
[INFO] hadoop-yarn-applications-unmanaged-am-launcher ..... SUCCESS [  1.836 s]
[INFO] hadoop-yarn-site ................................... SUCCESS [  0.037 s]
[INFO] hadoop-yarn-registry ............................... SUCCESS [  4.428 s]
[INFO] hadoop-yarn-project ................................ SUCCESS [  5.125 s]
[INFO] hadoop-mapreduce-client ............................ SUCCESS [  0.089 s]
[INFO] hadoop-mapreduce-client-core ....................... SUCCESS [ 18.387 s]
[INFO] hadoop-mapreduce-client-common ..................... SUCCESS [ 14.199 s]
[INFO] hadoop-mapreduce-client-shuffle .................... SUCCESS [  3.336 s]
[INFO] hadoop-mapreduce-client-app ........................ SUCCESS [  7.544 s]
[INFO] hadoop-mapreduce-client-hs ......................... SUCCESS [  6.352 s]
[INFO] hadoop-mapreduce-client-jobclient .................. SUCCESS [  9.120 s]
[INFO] hadoop-mapreduce-client-hs-plugins ................. SUCCESS [  1.641 s]
[INFO] Apache Hadoop MapReduce Examples ................... SUCCESS [  4.793 s]
[INFO] hadoop-mapreduce ................................... SUCCESS [  4.261 s]
[INFO] Apache Hadoop MapReduce Streaming .................. SUCCESS [  7.292 s]
[INFO] Apache Hadoop Distributed Copy ..................... SUCCESS [ 17.407 s]
[INFO] Apache Hadoop Archives ............................. SUCCESS [  1.828 s]
[INFO] Apache Hadoop Rumen ................................ SUCCESS [  4.916 s]
[INFO] Apache Hadoop Gridmix .............................. SUCCESS [  3.911 s]
[INFO] Apache Hadoop Data Join ............................ SUCCESS [  2.525 s]
[INFO] Apache Hadoop Ant Tasks ............................ SUCCESS [  1.984 s]
[INFO] Apache Hadoop Extras ............................... SUCCESS [  2.512 s]
[INFO] Apache Hadoop Pipes ................................ SUCCESS [  8.456 s]
[INFO] Apache Hadoop OpenStack support .................... SUCCESS [  4.533 s]
[INFO] Apache Hadoop Amazon Web Services support .......... SUCCESS [01:03 min]
[INFO] Apache Hadoop Client ............................... SUCCESS [  8.207 s]
[INFO] Apache Hadoop Mini-Cluster ......................... SUCCESS [  0.103 s]
[INFO] Apache Hadoop Scheduler Load Simulator ............. SUCCESS [  3.878 s]
[INFO] Apache Hadoop Tools Dist ........................... SUCCESS [ 10.011 s]
[INFO] Apache Hadoop Tools ................................ SUCCESS [  0.033 s]
[INFO] Apache Hadoop Distribution ......................... SUCCESS [ 32.250 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 15:24 min
[INFO] Finished at: 2021-07-16T17:41:57+08:00
[INFO] Final Memory: 216M/2108M
[INFO] ------------------------------------------------------------------------

编译成功后会打包，放在hadoop-dist/target
[root@bigdata-pro02 hadoop-2.6.0-src]# ll hadoop-dist/target/
总用量 529636
drwxr-xr-x 2 root root        28 7月  16 17:41 antrun
-rw-r--r-- 1 root root      1869 7月  16 17:41 dist-layout-stitching.sh
-rw-r--r-- 1 root root       642 7月  16 17:41 dist-tar-stitching.sh
drwxr-xr-x 9 root root       149 7月  16 17:41 hadoop-2.6.0
-rw-r--r-- 1 root root 180415644 7月  16 17:41 hadoop-2.6.0.tar.gz
-rw-r--r-- 1 root root      2778 7月  16 17:41 hadoop-dist-2.6.0.jar
-rw-r--r-- 1 root root 361917680 7月  16 17:41 hadoop-dist-2.6.0-javadoc.jar
drwxr-xr-x 2 root root        51 7月  16 17:41 javadoc-bundle-options
drwxr-xr-x 2 root root        28 7月  16 17:41 maven-archiver
drwxr-xr-x 2 root root         6 7月  16 17:41 test-dir

build失败注意删除下载库，重新下载
```