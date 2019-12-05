# Spring 事务管理 @Transactional 常用的配置属性

2018-08-24 20:19:55  更多

版权声明：本文为博主原创文章，遵循[ CC 4.0 BY-SA ](http://creativecommons.org/licenses/by-sa/4.0/)版权协议，转载请附上原文出处链接和本声明。本文链接：https://blog.csdn.net/zhen_6137/article/details/82024819

首先，先温习一下事务的隔离级别。

数据库系统提供了四种事务隔离级别供用户选择。不同的隔离级别采用不同的锁类型来实现，在四种隔离级别中，Serializable的隔离级别最高，Read Uncommited的隔离级别最低。大多数据库默认的隔离级别为Read Commited，如SqlServer，当然也有少部分数据库默认的隔离级别为Repeatable Read ，如Mysql

 Read Uncommited：读未提交数据(会出现脏读,不可重复读和幻读)。
 Read Commited：读已提交数据(会出现不可重复读和幻读)
 Repeatable Read：可重复读(会出现幻读)
 Serializable：串行化

脏读：一个事务读取到另一事务未提交的更新新据。
不可重复读：在同一事务中，多次读取同一数据返回的结果有所不同。换句话说就是，后续读取可以读到另一事务已提交的更新数据。相反，“可重复读”在同一事务中多次读取数据时，能够保证所读数据一样，也就是，后续读取不能读到另一事务已提交的更新数据。
幻读：一个事务读取到另一事务已提交的insert数据。

这些事务隔离级别可以去看spring源码 : org.springframework.transaction.annotation.Isolation
(用时，导入org.springframework.transaction.annotation.Isolation，再在Transactional括号里用如isolation = Isolation.DEFAULT)

 

**关于事务传播属性的说明：**
REQUIRED： 业务方法需要在一个事务中运行,如果方法运行时,已处在一个事务中,那么就加入该事务,否则自己创建一个新的事务.这是spring默认的传播行为.
SUPPORTS：如果业务方法在某个事务范围内被调用,则方法成为该事务的一部分,如果业务方法在事务范围外被调用,则方法在没有事务的环境下执行.
MANDATORY：只能在一个已存在事务中执行,业务方法不能发起自己的事务,如果业务方法在没有事务的环境下调用,就抛异常
REQUIRES_NEW：业务方法总是会为自己发起一个新的事务,如果方法已运行在一个事务中,则原有事务被挂起,新的事务被创建,直到方法结束,新事务才结束,原先的事务才会恢复执行.
NOT_SUPPORTED：声明方法需要事务,如果方法没有关联到一个事务,容器不会为它开启事务.如果方法在一个事务中被调用,该事务会被挂起,在方法调用结束后,原先的事务便会恢复执行.
NEVER：声明方法绝对不能在事务范围内执行,如果方法在某个事务范围内执行,容器就抛异常.只有没关联到事务,才正常执行.
NESTED：如果一个活动的事务存在,则运行在一个嵌套的事务中.如果没有活动的事务,则按REQUIRED属性执行.它使用了一个单独的事务, 这个事务拥有多个可以回滚的保证点.内部事务回滚不会对外部事务造成影响, 它只对DataSourceTransactionManager 事务管理器起效.
  ***\*这些事务传播属性可以去看spring源码 : org.springframework.transaction.annotation.Propagation
(用时，导入org.springframework.transaction.annotation.Propagation，再在Transactional括号里用如propagation = Propagation.REQUIRED)\**** 

 

**常用事务注释罗列：**
@Transactional (propagation = Propagation.REQUIRED,readOnly=true) //readOnly=true只读,不能更新,删除
@Transactional (propagation = Propagation.REQUIRED,timeout=30)//设置超时时间
@Transactional (propagation = Propagation.REQUIRED,isolation=Isolation.DEFAULT)//设置数据库隔离级别
//事务传播属性
@Transactional(propagation=Propagation.REQUIRED) //如果有事务,那么加入事务,没有的话新建一个(不写的情况下)
@Transactional(propagation=Propagation.NOT_SUPPORTED) //容器不为这个方法开启事务
@Transactional(propagation=Propagation.REQUIRES_NEW) //不管是否存在事务,都创建一个新的事务,原来的挂起,新的执行完毕,继续执行老的事务
@Transactional(propagation=Propagation.MANDATORY) //必须在一个已有的事务中执行,否则抛出异常
@Transactional(propagation=Propagation.NEVER) //必须在一个没有的事务中执行,否则抛出异常(与Propagation.MANDATORY相反)
@Transactional(propagation=Propagation.SUPPORTS) //如果其他bean调用这个方法,在其他bean中声明事务,那就用事务.如果其他bean没有声明事务,那就不用事务.

**@Transactional 的所有可选属性如下:**

属性 类型 默认值 说明
propagation Propagation枚举 REQUIRED 事务传播属性 (下有说明)
isolation isolation枚举 DEFAULT 事务隔离级别 (另有说明)
readOnly boolean false 是否只读
timeout int -1 超时(秒)
rollbackFor Class[] {} 需要回滚的异常类
rollbackForClassName String[] {} 需要回滚的异常类名
noRollbackFor Class[] {} 不需要回滚的异常类
noRollbackForClassName String[] {} 不需要回滚的异常类名

 

 **Transactional 注解的属性**

| 属性                                     | 类型                                        | 描述                                                         |
| :--------------------------------------- | :------------------------------------------ | :----------------------------------------------------------- |
| 传播性                                   | 枚举型：Propagation                         | 可选的传播性设置 （默认值：PROPAGATION_REQUIRED ）           |
| 隔离性                                   | 枚举型：Isolation                           | 可选的隔离性级别（默认值：ISOLATION_DEFAULT ）               |
| 只读性                                   | 布尔型                                      | 读写型事务 vs. 只读型事务（默认值：false ，即只读型事务）    |
| 回滚异常类（rollbackFor）                | 一组 Class 类的实例，必须是Throwable 的子类 | 一组异常类，遇到时 **确保** 进行回滚。默认情况下checked exceptions不进行回滚，仅unchecked exceptions（即RuntimeException 的子类）才进行事务回滚。 |
| 回滚异常类名（rollbackForClassname）     | 一组 Class 类的名字，必须是Throwable 的子类 | 一组异常类名，遇到时 **确保** 进行回滚                       |
| 不回滚异常类（noRollbackFor）            | 一组 Class 类的实例，必须是Throwable 的子类 | 一组异常类，遇到时确保 **不** 回滚。                         |
| 不回滚异常类名（noRollbackForClassname） | 一组 Class 类的名字，必须是Throwable 的子类 | 一组异常类，遇到时确保 **不** 回滚                           |

文章最后发布于: 2018-08-24 20:19:55