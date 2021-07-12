# hiverserver2/beeline
1. 启动服务端： $ bin/hiveserver2 
2. 启动客户端： $ bin/beeline 
3. 客户端连接hive： !connect jdbc:hive2://localhost:10000 root root
## docker内没有root密码的问题
```
docker exec -it hive /bin/bash 
root@b4c3d4354fae:~# passwd
Enter new UNIX password: 
Retype new UNIX password: 
passwd: password updated successfully
```
# 处理错误：User: root is not allowed to impersonate root (state=08S01,code=0)。
修改hadoop 配置文件 etc/hadoop/core-site.xml,加入如下配置项
```
<property>
    <name>hadoop.proxyuser.root.hosts</name>
    <value>*</value>
</property>
<property>
    <name>hadoop.proxyuser.root.groups</name>
    <value>*</value>
</property>
```
如果是其他用户名，例如‘cjx’，则需要把配置项中的root 改为 ‘cjx’