# 在容器中编译(添加阿里云docker加速)
```
docker search centos
docker pull centos:6
docker run -it --net bigdata --ip 172.168.0.250 --name centos6 centos:6 bash

cd /etc/yum.repos.d/
rm -f ./*
docker cp CentOS6-Base-163.repo 174a02239eb2:/etc/yum.repos.d/
vi /etc/resolv.conf
添加内容：
nameserver 8.8.8.8
nameserver 8.8.4.4

➜  modules docker cp apache-maven-3.3.9-bin.tar.gz 174a02239eb2:/opt/modules/
➜  modules docker cp jdk-7u79-linux-x64.gz 174a02239eb2:/opt/modules/
➜  modules docker cp protobuf-2.5.0.tar 174a02239eb2:/opt/modules/
➜  modules docker cp snappy-1.1.3.tar 174a02239eb2:/opt/modules/
➜  modules docker cp hadoop-2.6.0-src.tar.gz 174a02239eb2:/opt/modules/

tar -xf hadoop-2.6.0-src.tar.gz -C ./
tar -xf apache-maven-3.3.9-bin.tar.gz -C ./
tar -xf jdk-7u79-linux-x64.gz -C ./
tar -xf protobuf-2.5.0.tar -C ./
tar -xf snappy-1.1.3.tar -C ./

rm ./*.gz
rm ./*.tar

vi /etc/profile
export JAVA_HOME=/opt/modules/jdk1.7.0_79
export MAVEN_HOME=/opt/modules/apache-maven-3.3.9
export PATH=$JAVA_HOME/bin:$PATH:$MAVEN_HOME/bin

source /etc/profile

java -version
java version "1.7.0_79"
Java(TM) SE Runtime Environment (build 1.7.0_79-b15)
Java HotSpot(TM) 64-Bit Server VM (build 24.79-b02, mixed mode)

mvn -version
Apache Maven 3.3.9 (bb52d8502b132ec0a5a3f4c09453c07478323dc5; 2015-11-10T16:41:47+00:00)
Maven home: /opt/modules/apache-maven-3.3.9
Java version: 1.7.0_79, vendor: Oracle Corporation
Java home: /opt/modules/jdk1.7.0_79/jre
Default locale: en_US, platform encoding: ANSI_X3.4-1968
OS name: "linux", version: "5.4.0-77-generic", arch: "amd64", family: "unix"


```