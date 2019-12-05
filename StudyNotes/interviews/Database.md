# 密集索引和稀疏索引的区别

2019-01-03 20:59:43  文章标签： [密集索引和稀疏索引](https://so.csdn.net/so/search/s.do?q=密集索引和稀疏索引&t=blog) 更多

版权声明：本文为博主原创文章，遵循[ CC 4.0 BY-SA ](http://creativecommons.org/licenses/by-sa/4.0/)版权协议，转载请附上原文出处链接和本声明。本文链接：https://blog.csdn.net/fansenjun/article/details/85647734

### 区别

- 密集索引文件中的每个搜索码值都对应一个索引值
- 稀疏索引文件只为索引码的某些值建立索引项

![img](https://img-blog.csdnimg.cn/20190102230744987.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZhbnNlbmp1bg==,size_16,color_FFFFFF,t_70)

密集索引的定义：叶子节点保存的不只是键值，还保存了位于同一行记录里的其他列的信息，由于密集索引决定了表的物理排列顺序，一个表只有一个物理排列顺序，所以一个表只能创建一个密集索引

稀疏索引：叶子节点仅保存了键位信息以及该行数据的地址，有的稀疏索引只保存了键位信息机器主键

mysam存储引擎，不管是主键索引，唯一键索引还是普通索引都是稀疏索引

innodb存储引擎：有且只有一个密集索引。密集索引的选取规则如下：

- 若主键被定义，则主键作为密集索引
- 如果没有主键被定义，该表的第一个唯一非空索引则作为密集索引
- 若不满足以上条件，innodb内部会生成一个隐藏主键（密集索引）
- 非主键索引存储相关键位和其对应的主键值，包含两次查找

![img](https://img-blog.csdnimg.cn/20190103205351974.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZhbnNlbmp1bg==,size_16,color_FFFFFF,t_70)