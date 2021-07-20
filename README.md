# Hive
# 版本
hive-2.3.4-bin/
# 启动
## Hadoop
```
service ssh start
export JAVA_HOME=/opt/modules/jdk1.8.0_271
export PATH=$JAVA_HOME/bin:$PATH
/opt/modules/hadoop-2.6.0/sbin/start-dfs.sh
/opt/modules/hadoop-2.6.0/sbin/start-yarn.sh
jps
```
## Hive
```
service ssh start
export JAVA_HOME=/opt/modules/jdk1.8.0_271
export PATH=$JAVA_HOME/bin:$PATH
/opt/modules/hive-2.3.4-bin/bin/hive
```
