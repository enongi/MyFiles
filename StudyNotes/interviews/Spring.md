## Spring

### spring 的特点

【参考：https://blog.csdn.net/hht006158/article/details/80181207】
1、方便解耦，简化开发：通过spring提供的IOC容器，我们可以将对象之间的依赖关系交由spring来控制，避免由硬编码所造成的过度程序耦合
2、AOP编程支持：通过spring提供的AOP功能，方便的进行面向切面编程，把应用业务逻辑和系统服务分开，提高代码重用效率
3、声明事务支持：spring提供一个持续的事务管理接口，在spring中通过声名式方式灵活的进行事务管理，提高开发效率和质量
4、方便程序的测试：可以通过非容器依赖的方式进行几乎所有的测试工作，例如spring对Junit4的支持，可以通过注解方式测试spring程序
5、方便集成各种优秀框架：spring提供了对各种优秀框架的直接支持，简化了各种框架的集成难度
6、降低了JavaEE API的使用难度

### spring的优点

1、低侵入式设计，代码污染极低
2、独立于各种应用服务器，基于spring应用框架，可以实现正真的Write once ，run anywhere 
3、spring的DI机制降低了业务对象替换的复杂性，提高了组件之间的松耦合
4、spring的AOP支持允许将一些通用任务如安全，事务，日志等进行集中制管理，从而提供了更好的复用
5、spring的ORM和DAO提供了与第三方持久层框架的良好整合，并简化了底层数据库访问
6、spring并不强制应用完全依赖spring，开发者可以自由的选择使用spring的部分或者全部

### spring的核心

【参考：https://www.cnblogs.com/dandelZH/p/9038724.html】
1、控制反转 （Inverse of Control ,IOC）依赖注入（Dependency Injection, DI）
	通常程序功能的实现由两个或多个对象共同协调完成，通过DI，对象的依赖关系将由系统中负责协调各对象的第三方组件在创建对象的时候进行设定
	对象无需自行创建或管理他们的依赖关系
	依赖注入将对象所依赖的关系自动交给目标对象，而不是由对象自己去获取依赖，这样达到了松耦合的目的
	依赖注入的方式：
		构造器注入
		属性注入
		字段注入（注解注入）
	应用上下文
　　 Spring通过应用上下文装配bean的定义并把它们组装起来。
　　 Spring自带了多种应用上下文的实现，下面是最常用的几个：
　　　　1）ClassPathXmlApplicationContext
　　　　2）AnnotationConfigApplicationContext
　　　　3）AnnotationConfigWebApplicationContext
　　　　4）FileSystemXmlApplicationContext
　　　　5）XmlWebApplicationContext　　

装配bean

  　　1. Spring装配bean的三种方案（按建议的优先使用顺序）：
       　　1） 自动化装配
       　　　　2） 通过Java代码配置
       　　　　3） 通过XML配置
       　　以上三种配置方案，建议尽可能地使用自动化配置的机制，显示配置越少越好。
  　　2. 自动化装配bean
       　　2.1 Spring从两个角度来实现自动化装配
       　　　　　　1） 组件扫描：Spring会自动发现应用上下文中所创建的bean。
       　　　　　　2） 自动装配：Spring自动满足bean之间的依赖。
       　　　　2.2 Spring自动化装配主要使用的注解：
       　　　　　　1） @Component：标志要创建的bean
       　　　　　　2） @ComponentScan：启用组件扫描
       		3)  @AutoWired：将bean的依赖注入。( 也可用源于Java依赖注入规范的 @Inject注解)　
  　　3. 通过Java代码装配bean
       　　尽管在很多场景下通过组件扫描和自动装配来实现Spring的自动化配置时更为推荐的方式，
       	但是在如使用第三方库中的组件（因为自动化配置需要将注解写到具体的类上，而第三方库的类都是封装、编译的），这时就可以使用Java配置。
       　　　　通过Java显示配置Spring：
       　　　　　　1） 创建配置类：使用 @Configuration注解声明类为配置类。
       　　　　　　2） 声明bean：  使用 @Bean注解在JavaConfig配置类中声明bean。 

  　　4. 通过XML装配bean
       　　1） 创建XML文件，并以<bean>元素为根，在XML配置文件的顶部需要引入多个XML模式（XSD文件），这些文件定义了配置Spring的XML元素。
       　　　　　　建议使用Spring Tool Suite插件，通过File->new->Spring Bean Configuration File ，能够创建Spring XML配置文件，并可以选择可用的配置命名空间（如aop）。
       　　　　2） 使用<bean>元素声明bean
       　　　　3） 使用<constructor-arg>元素或c-命名空间的<c:_ref=""/>借助构造器注入依赖。
       　　　　4） 使用<property>元素或p-命名空间的<p:name-ref=""/>装配属性。
       2、面向切面编程（Aspect-Oriented Programming, AOP）
       在软件开发中，散布于应用中多处的功能被称为横切关注点。通常来讲，这些横切关注点从概念上是与应用的业务逻辑相分离的，把这些横切关注点与业务逻辑相分离、实现横切关注点和它们所影响的对象之间的解耦正是面向切面编程（AOP）要解决的问题。
  　　5. 定义AOP术语
       　　1）通知（Advice）
       　　　　　　通知定义了切面是什么及何时使用。
       　　　　　　通知有5种类型：
       　　　　　　　　前置通知（Before）
       　　　　　　　　后置通知（After）
       　　　　　　　　环绕通知（Around）
       　　　　　　　　返回通知（After-returning）
       　　　　　　　　异常通知（After-throwing）
       　　　　2）连接点（Join-point）
       　　　　　　连接点是应用执行过程中能够插入切面的一个点。
       　　　　3）切点（PoinCut）
       　　　　　　切点定义了何处使用。切点的定义会匹配通知所要织入的一个或多个连接点。
       　　　　4）切面（Aspect）
       　　　　　　切面是通知和切点的结合，通知和切点共同定义了切面的全部内容-它是什么，在何时和何处完成其功能。
       　　　　5）引入（Intriduction）
       　　　　　　引入允许我们在无需修改现有类的情况下，向现有的类添加新方法或属性，使它们具有新的行为和属性。
       　　　　6）织入（Weaving）
       　　　　　　织入是把切面应用到目标对象并创建新的代理对象的过程，切面在指定的连接点被织入到目标对象。
       　　　　　　在目标对象的生命周期有多个点可以织入：
       　　　　　　　　编译期
       　　　　　　　　类加载期
       　　　　　　　　运行期


依赖倒置原则（Dependency Inverse Principle）

### spring Bean的生命周期 

【参考:https://www.jianshu.com/p/1dec08d290c1】
Spring Bean的生命周期分为四个阶段和多个扩展点，扩展点又分为影响多个bean和影响单个bean。具体如下：
四个阶段：
	实例化 Instantiation
	属性赋值 Populate
	初始化 Initialization
	销毁 Destruction
多个扩展点
	影响多个Bean：
		BeanPostProcessor
		InstantiationAwareBeanPostProcessor
	影响单个Bean：
	  Aware：
	    Aware Group1：
		  BeanNameAware
		  BeanClassLoaderAware
		  BeanFactoryAware
		Aware Group2：
		  EnvironmentAware
		  EmbeddedValueResolverAware
		  ApplicationContextAware(ResourceLoaderAware/ApplcationEventPublisherAware/MessageSourceAware)
	  生命周期：
		InitializingBean
		DisposableBean

### BeanFactory 和 ApplicationContext的区别

BeanFactory和ApplicationContext是Spring的两大核心接口，都可以当做Spring的容器，ApplicationContext是BeanFactory的子接口
1、BeanFactory是Spring的最底层接口，包含了Bean的各种定义，读取bean的配置文档，管理bean的加载、实例化，控制bean的生命周期，
维护bean之间的依赖关系。ApplicationContext接口作为BeanFactory的派生除了提供BeanFactory所具有的基本功能外，
还提供了更完整的框架功能：
	继承MessageSource，因此支持国际化
		BeanFactory是不支持国际化功能的，因为BeanFactory没有扩展Spring中MessageResource接口。
		相反，由于ApplicationContext扩展了MessageResource接口，因而具有消息处理的能力(i18N)
	统一的资源文件访问方式
		ApplicationContext扩展了ResourceLoader(资源加载器)接口，从而可以用来加载多个Resource，
		而BeanFactory是没有扩展ResourceLoader
	提供在监听器中注册bean的事件
		ApplicationContext的事件机制主要通过ApplicationEvent和ApplicationListener这两个接口来提供的，和java swing中的事件机制一样。即当ApplicationContext中发布一个事件的时，所有扩展了ApplicationListener的Bean都将会接受到这个事件，并进行相应的处理。
		Spring提供了部分内置事件，主要有以下几种：
			ContextRefreshedEvent ：ApplicationContext发送该事件时，表示该容器中所有的Bean都已经被装载完成，
			此ApplicationContext已就绪可用
			ContextStartedEvent：生命周期 beans的启动信号
			ContextStoppedEvent: 生命周期 beans的停止信号
			ContextClosedEvent：ApplicationContext关闭事件，则context不能刷新和重启，
			从而所有的singleton bean全部销毁(因为singleton bean是存在容器缓存中的)
		虽然，spring提供了许多内置事件，但用户也可根据自己需要来扩展spriong中的事物。注意，要扩展的事件都要实现ApplicationEvent接口
	同时加载多个配置文件
		ApplicationContext扩展了ResourceLoader(资源加载器)接口，从而可以用来加载多个Resource，而BeanFactory是没有扩展ResourceLoader
	载入多个（有继承关系）上下文，使得每个上下文都专注于一个特定的层次，比如应用的web层
2、BeanFactory采用延迟加载的方式注入bean，即只有在使用到某个bean(调用getBean()方法)，才对Bean进行加载实例化，这样我们就不能发现
一些存在的Spring的配置问题。如果bean的某一属性没有注入，BeanFactory加载后，直到第一次使用(调用getBean()方法)才会抛出异常
ApplicationContext是在容器启动时，一次性创建了所有的bean。。这样，在容器启动时，我们就可以发现Spring中存在的配置错误，
这样有利于检查所依赖属性是否注入。 ApplicationContext启动后预载入所有的单实例Bean，通过预载入单实例bean ,确保当你需要的时候，
你就不用等待，因为它们已经创建好了。
相对于基本的BeanFactory，ApplicationContext 唯一的不足是占用内存空间。当应用程序配置Bean较多时，程序启动较慢。
3、BeanFactory通常以编程的方式被创建，ApplicationContext还能以声明的方式被创建，如使用ContextLoader
	ContextLoader有两个实现：ContextLoaderListener和ContextLoaderServlet
4、BeanFactory和ApplicationContext都支持BeanPostProcessor、BeanFactoryPostProcessor的使用，两者的区别是
BeanFactory需要手动注册，而ApplicationContext是自动注册



