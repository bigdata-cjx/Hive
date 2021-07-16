# hadoop编译(添加 snappy 库)
## 检查本地库，查看是否有snappy压缩库
```
root@bigdata:/opt/modules/hadoop-2.6.0# bin/hadoop checknative
21/07/12 09:13:48 WARN bzip2.Bzip2Factory: Failed to load/initialize native-bzip2 library system-native, will use pure-Java version
21/07/12 09:13:48 INFO zlib.ZlibFactory: Successfully loaded & initialized native-zlib library
21/07/12 09:13:48 ERROR snappy.SnappyCompressor: failed to load SnappyCompressor
java.lang.UnsatisfiedLinkError: Cannot load libsnappy.so.1 (libsnappy.so.1: cannot open shared object file: No such file or directory)!
	at org.apache.hadoop.io.compress.snappy.SnappyCompressor.initIDs(Native Method)
	at org.apache.hadoop.io.compress.snappy.SnappyCompressor.<clinit>(SnappyCompressor.java:61)
	at org.apache.hadoop.io.compress.SnappyCodec.isNativeCodeLoaded(SnappyCodec.java:79)
	at org.apache.hadoop.util.NativeLibraryChecker.main(NativeLibraryChecker.java:82)
Native library checking:
hadoop:  true /opt/modules/hadoop-2.6.0/lib/native/libhadoop.so.1.0.0
zlib:    true /lib/x86_64-linux-gnu/libz.so.1
snappy:  false 
lz4:     true revision:99
bzip2:   false 
```
## 下载Hadoop源码，解压
https://archive.apache.org/dist/hadoop/common/hadoop-2.6.0/hadoop-2.6.0-src.tar.gz
```
➜  modules tar -xf hadoop-2.6.0-src.tar.gz -C ./
```
hadoop-2.6.0-src/BUILDING.txt: 编译文档
```
Requirements:

* Unix System
* JDK 1.6+
* Maven 3.0 or later
* Findbugs 1.3.9 (if running findbugs)
* ProtocolBuffer 2.5.0
* CMake 2.6 or newer (if compiling native code), must be 3.0 or newer on Mac
* Zlib devel (if compiling native code)
* openssl devel ( if compiling native hadoop-pipes )
* Internet connection for first build (to fetch all Maven and Hadoop dependencies)
```
## 查看本地环境
JAVA(使用jdk7比较省事，使用jdk8的话参考：http://cxy7.com/articles/2017/06/24/1498294228116.html)
```
java -version
java version "1.7.0_79"
Java(TM) SE Runtime Environment (build 1.7.0_79-b15)
Java HotSpot(TM) 64-Bit Server VM (build 24.79-b02, mixed mode)
```
MAVEN
```
tar -xf apache-maven-3.3.9-bin.tar.gz -C /opt/modules
vim /etc/profile
#MAVEN_HOME
export MAVEN_HOME=/opt/modules/apache-maven-3.3.9
export PATH=$MAVEN_HOME/bin:$PATH

source /etc/profile
$ mvn -version
Apache Maven 3.3.9 (bb52d8502b132ec0a5a3f4c09453c07478323dc5; 2015-11-11T00:41:47+08:00)
Maven home: /opt/modules/apache-maven-3.3.9
Java version: 1.8.0_271, vendor: Oracle Corporation
Java home: /opt/modules/jdk1.8.0_271/jre
Default locale: zh_CN, platform encoding: UTF-8
OS name: "linux", version: "5.4.0-74-generic", arch: "amd64", family: "unix"

apache-maven-3.3.9 vim conf/settings.xml
添加内容：
<mirror>
  <id>alimaven</id>
  <name>aliyun maven</name>
  <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
  <mirrorOf>central</mirrorOf>
</mirror>
```
ProtoBuf
```
tar -xf protobuf-2.5.0.tar -C /opt/modules
protobuf-2.5.0 su
$ ./configure
$ make
$ make install
```
Snappy
```
tar -xf snappy-1.1.3.tar -C /opt/modules
snappy-1.1.3 su
$ ./configure
$ make
$ make install
```
CMake
```
centos: sudo yum install cmake
ubuntu: sudo apt install cmake
```
## 编译
```
➜  hadoop-2.6.0-src source /etc/profile
$ mvn clean package  -Pdist,native -DskipTests -Dtar -Dbundle.snappy -Dsnappy.lib=/usr/local/lib 
```
### Ubuntu编译Hadoop源码异常总结（https://blog.csdn.net/u013078295/article/details/52211903）
#### 问题
```
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-antrun-plugin:1.7:run (make) on project hadoop-common: An Ant BuildException has occured: exec returned: 1
[ERROR] around Ant part ...<exec dir="/opt/modules/hadoop-2.6.0-src/hadoop-common-project/hadoop-common/target/native" executable="cmake" failonerror="true">... @ 4:133 in /opt/modules/hadoop-2.6.0-src/hadoop-common-project/hadoop-common/target/antrun/build-main.xml
[ERROR] -> [Help 1]
```
#### 原因
```
Hadoop snappy对gcc版本还有要求,gcc4.4

$ gcc --version
gcc (Ubuntu 7.5.0-3ubuntu1~18.04) 7.5.0
Copyright (C) 2017 Free Software Foundation, Inc.
This is free software; see the source for copying conditions.  There is NO
warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
```