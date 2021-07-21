当job提交到Yarn之后，每个job中的map任务或者reduce任务，都会分配一个container容器，map或者reduce任务的执行的资源由container提供,
每个map任务或者reduce任务执行开始前都要初始化container，任务执行完成之后要销毁container。
如果我们采用jvm重用，那么每一个任务执行完成之后，不需要销毁容器，container直接接受新的map任务或者reduce任务。

# mapreduce.job.jvm.numtasks(通过YARN设置)
## 默认值：1 （不重用）  
## >1 重用  
建议值：3或者5 不要超过8