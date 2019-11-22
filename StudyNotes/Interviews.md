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
　　1. 定义AOP术语
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



## Java集合类

### ArrayList 和 LinkedList 的区别
1、ArrayList底层使用数组存储元素，因此在查询的时候速度快，直接返回该位置元素即可，时间复杂度为O(1)；而LinkedList底层使用的是双向链表
存储元素，在查询时需要从头或者尾遍历至查询元素，时间复杂度为O(n/2)
2、由于存储方式的不同，ArrayList插入或者删除元素之后，需要移动插入或者删除位置之后的所有元素，速度比较慢，时间复杂度为O(n),而LinkedList
只需要移动‘指针’(更改前驱和后驱节点指向地址)即可，时间复杂度为O(1)
3、综上，ArrayList适用于较多查询，获取某位置的元素的场景，LinkedList适用于频繁插入删除，较少查询的场景
ArrayList使用数组存储元素，每次扩容后容量为之前容量的1.5倍，而且扩容后会有一个复制所有元素的操作，会比较费时间；
由于LinkedList使用双向链表存储数据，因此不需要扩容机制
考虑到空间LinkedList使用空间大于ArrayList 因为ArrayList每个位置只存储元素本身，LinkedList存储了元素本身和前一节点及后一节点的地址

### List Set Map 的区别
List和Set是Collection的子接口，Map不是Collection的子接口
List：可以允许重复元素出现，可以插入多个null元素，是一个有序容器，保持了每个元素的插入顺序，输出顺序同插入顺序
	常见的实现类有ArrayList、LinkedList、Vector，ArrayList提供了使用索引随意访问，底层使用数组，优查询，劣增删，LinkedList常用于
	多新增和删除场景，底层使用双向链表
Set：不允许重复元素，无序容器，只允许有一个null元素，Set常见的实现类有HashSet，LinkedHashSet以及TreeSet。最流行的是基于HashMap实现的HashSet
	(HashSet就是一个没有Value的HashMap)，TreeSet还实现了SortedSet接口，因此TreeSet是根据其compare()和compareTo()方法排序的有序容器
Map：Map不是Collection的子接口，Map每个Entry都持有两个对象，一个键一个值，可以持有相同的值对象，但是键对象必须要唯一
	常见的实现类有HashMap，LinkedHashMap，HashTable，TreeMap

### HashMap、LinkedHashMap和TreeMap之间的区别
HashMap 线程不安全，数据无序，数据结构是 数组+链表+红黑树（在JDK8中如果链表长度大于8的时候链表转换为红黑树）
LinkedHashMap 线程不安全，数据有序，数据结构为双向链表+HashMap
TreeMap 线程不安全，数据有序且可以对数据进行排序，数据结构为红黑树
HashMap是最常用的Map，它根据hashCode值存储数据，根据键可以直接获取值，具有很快的访问速度。HashMap只允许一条键为null的记录，
允许多条值为null的记录，HashMap不支持线程同步，即任意时刻可以有多个线程同时操作同一个HashMap，可能会导致数据不一致，如需要同步可以使用
Collections.synchronizedMap(HashMap map)方法使HashMap具有同步能力
LinkedHashMap保存了记录的插入顺序，在用Iterator遍历LinkedHashMap时，先插入的数据会先得到，遍历时会比HashMap慢，有HashMap的全部特性
TreeMap能够把它保存的记录根据键排序，默认是按升序排序，也可以指定排序的比较器。当用Iteraor遍历TreeMap时，得到的记录是排过序的。
TreeMap的键和值都不能为空。它的get或put操作的时间复杂度是O(log(n))

### HashMap和Hashtable的区别

1、继承的父类不同，HashMap和Hashtable都实现了Map接口，但是HashMap继承了AbstractMap类，而Hashtable继承了Dictionary类
2、线程的安全性不同，HashMap是线程不安全的，Hashtable是线程安全的，方法都是同步的
3、是否提供contains() 方法，HashMap删除contains() 方法，由containsValue()和containsKey()方法代替
	Hashtable保留了contains()方法，同时有containsValue()和containsKey()方法，其中contains()方法和containsValue()方法功能相同
4、key和value是否允许null值
	HashMap中，null可以作为键，这样的键只有一个；可以有一个或多个键所对应的值为null。当get()方法返回null值时，
	可能是 HashMap中没有该键，也可能使该键所对应的值为null。因此，在HashMap中不能由get()方法来判断HashMap中是否存在某个键，
	而应该用containsKey()方法来判断。
	Hashtable中，key和value都不允许出现null值。但是如果在Hashtable中有类似put(null,null)的操作，编译同样可以通过，
	因为key和value都是Object类型，但运行时会抛出NullPointerException异常，这是JDK的规范规定的
5、两个遍历方式的内部实现上不同
    Hashtable、HashMap都使用了 Iterator。而由于历史原因，Hashtable还另外使用了Enumeration的方式 。
6、hash值不同
      哈希值的使用不同，HashTable直接使用对象的hashCode。而HashMap重新计算hash值。
      hashCode是jdk根据对象的地址或者字符串或者数字算出来的int类型的数值。
      Hashtable计算hash值，直接用key的hashCode()，而HashMap重新计算了key的hash值，Hashtable在求hash值对应的位置索引时，用取模运算，
	  而HashMap在求位置索引时，则用与运算，且这里一般先用hash&0x7FFFFFFF后，再对length取模，&0x7FFFFFFF的目的是为了将负的hash值
	  转化为正值，因为hash值有可能为负数，而&0x7FFFFFFF后，只有符号外改变，而后面的位都不变
7、内部实现使用的数组初始化和扩容方式不同
      Hashtable在不指定容量的情况下的默认容量为11，而HashMap为16，Hashtable不要求底层数组的容量一定要为2的整数次幂，而HashMap则要求一定为2的整数次幂。
      Hashtable扩容时，将容量变为原来的2倍加1，而HashMap扩容时，将容量变为原来的2倍。
      Hashtable和HashMap它们两个内部实现方式的数组的初始大小和扩容的方式。HashTable中hash数组默认大小是11，增加的方式是 old*2+1。

### ConcurrentHashMap

JDK5之后提供了ConcurrentHashMap来替代HashTable。ConcurrentHashMap是对HashTable的优化。ConcurrentHashMap(采用分段锁，线程安全)

1. 底层实现采用 分段数组+链表
2. 默认将整个Map分成16片（Segment，可调参数）
3. Hashtable进行修改操作时，要锁住整张表，以保证数据安全；而ConcurrentHashMap只需锁住要修改数据所在的那一分片（锁分离技术）
4. 有时候某些操作需要涉及到多个分片，如获取Map的size、查找某个值是否存在，此时只需要按顺序将每一片锁住，操作完毕后，再按顺序将锁释放
5. ConcurrentHashMap是段内插入前扩容，即插入前检查当前有效存储数组的hash值个数是否超过负载因子（默认为总容量的75%，可调参数），
若超过，进行段内扩容

## Mybatis和Hibernate的本质区别和使用场景

Hibernate是一个标准化的ORM框架，入门的门槛较高，不需要程序写sql，语句自动生成，sql的优化修改比较困难
	应用场景：适用于中小企业需求变化不多的场景，比如后台管理系统，ERP,ORM,OA.
Mybatis专注于sql本身，程序员要自己写sql语句，sql的修改与优化比较方便，是一个不完全的ORM框架，虽然程序员自己写sql，但它自身也可以实现映射
	应用场景：适用于需求变化较多的项目，如互联网项目等



## JVM（Java Virtual Machine）

 JVM = 类加载器 classloader + 执行引擎 execution engine + 运行时数据区域 runtime data area

### Java类加载机制 

参考【 https://blog.csdn.net/m0_38075425/article/details/81627349 】

​		当程序主动使用某个类时，如果该类还未被加载到内存中，则JVM会通过加载、链接、初始化3个步骤对该类进行初始化。如果没有意外JVM会连续完成三个步骤，所以有时也将三个步骤统称为类加载或者类初始化

**一、类加载过程**
1.加载    
		加载指的是将类的class文件读入到内存，并为之创建一个java.lang.Class对象，也就是说，当程序中使用任何类时，系统都会为之建立一个java.lang.Class对象。

​		类的加载由类加载器完成，类加载器通常由JVM提供，这些类加载器也是前面所有程序运行的基础，JVM提供的这些类加载器通常被称为系统类加载器。除此之外，开发者可以通过继承ClassLoader基类来创建自己的类加载器。

通过使用不同的类加载器，可以从不同来源加载类的二进制数据，通常有如下几种来源。
		1).从本地文件系统加载class文件，这是前面绝大部分示例程序的类加载方式。
		2).从JAR包加载class文件，这种方式也是很常见的，前面介绍JDBC编程时用到的数据库驱动类就放在JAR文件中，JVM可以从JAR文件中直接加载该class文件。
		3).通过网络加载class文件。
		4).把一个Java源文件动态编译，并执行加载。
 类加载器通常无须等到“首次使用”该类时才加载该类，Java虚拟机规范允许系统预先加载某些类。

2.链接
    	当类被加载之后，系统为之生成一个对应的Class对象，接着将会进入连接阶段，连接阶段负责把类的二进制数据合并到JRE中。类连接又可分为如下3个阶段。

​		1)验证：验证阶段用于检验被加载的类是否有正确的内部结构，并和其他类协调一致。Java是相对C++语言是安全的语言，例如它有C++不具有的数组越界的检查。这本身就是对自身安全的一种保护。验证阶段是Java非常重要的一个阶段，它会直接的保证应用是否会被恶意入侵的一道重要的防线，越是严谨的验证机制越安全。验证的目的在于确保Class文件的字节流中包含信息符合当前虚拟机要求，不会危害虚拟机自身安全。其主要包括四种验证，文件格式验证，元数据验证，字节码验证，符号引用验证。

四种验证做进一步说明：
	文件格式验证：主要验证字节流是否符合Class文件格式规范，并且能被当前的虚拟机加载处理。例如：主，次版本号是否在当前虚拟机处理的范围之内。常量池中是否有不被支持的常量类型。指向常量的中的索引值是否存在不存在的常量或不符合类型的常量。
	元数据验证：对字节码描述的信息进行语义的分析，分析是否符合java的语言语法的规范。
	字节码验证：最重要的验证环节，分析数据流和控制，确定语义是合法的，符合逻辑的。主要的针对元数据验证后对方法体的验证。保证类方法在运行时不会有危害出现。
	符号引用验证：主要是针对符号引用转换为直接引用的时候，是会延伸到第三解析阶段，主要去确定访问类型等涉及到引用的情况，主要是要保证引用一定会被访问到，不会出现类等无法访问的问题。

   	2)准备：类准备阶段负责为类的静态变量分配内存，并设置默认初始值。

   	3)解析：将类的二进制数据中的符号引用替换成直接引用。说明一下：符号引用：符号引用是以一组符号来描述所引用的目标，符号可以是任何的字面形式的字面量，只要不会出现冲突能够定位到就行。布局和内存无关。直接引用：是指向目标的指针，偏移量或者能够直接定位的句柄。该引用是和内存中的布局有关的，并且一定加载进来的。

3.初始化
    初始化是为类的静态变量赋予正确的初始值，准备阶段和初始化阶段看似有点矛盾，其实是不矛盾的，如果类中有语句：private static int a = 10，它的执行过程是这样的，首先字节码文件被加载到内存后，先进行链接的验证这一步骤，验证通过后准备阶段，给a分配内存，因为变量a是static的，所以此时a等于int类型的默认初始值0，即a=0,然后到解析（后面在说），到初始化这一步骤时，才把a的真正的值10赋给a,此时a=10。

**二、类加载时机**
1.创建类的实例，也就是new一个对象
2.访问某个类或接口的静态变量，或者对该静态变量赋值
3.调用类的静态方法
4.反射（Class.forName("com.lyj.load")）
5.初始化一个类的子类（会首先初始化子类的父类）
6.JVM启动时标明的启动类，即文件名和类名相同的那个类    
     除此之外，下面几种情形需要特别指出：
对于一个final类型的静态变量，如果该变量的值在编译时就可以确定下来，那么这个变量相当于“宏变量”。Java编译器会在编译时直接把这个变量出现的地方替换成它的值，因此即使程序使用该静态变量，也不会导致该类的初始化。反之，如果final类型的静态Field的值不能在编译时确定下来，则必须等到运行时才可以确定该变量的值，如果通过该类来访问它的静态变量，则会导致该类被初始化。

**三、类加载器**
   	 类加载器负责加载所有的类，其为所有被载入内存中的类生成一个java.lang.Class实例对象。一旦一个类被加载如JVM中，同一个类就不会被再次载入了。正如一个对象有一个唯一的标识一样，一个载入JVM的类也有一个唯一的标识。在Java中，一个类用其全限定类名（包括包名和类名）作为标识；但在JVM中，一个类用其全限定类名和其类加载器作为其唯一标识。例如，如果在pg的包中有一个名为Person的类，被类加载器ClassLoader的实例kl负责加载，则该Person类对应的Class对象在JVM中表示为(Person.pg.kl)。这意味着两个类加载器加载的同名类：（Person.pg.kl）和（Person.pg.kl2）是不同的、它们所加载的类也是完全不同、互不兼容的。

JVM预定义有三种类加载器，当一个 JVM启动的时候，Java开始使用如下三种类加载器：
		1)根类加载器（bootstrap class loader）:它用来加载 Java 的核心类，是用原生代码来实现的，并不继承自 java.lang.ClassLoader（负责加载$JAVA_HOME中jre/lib/rt.jar里所有的class，由C++实现，不是ClassLoader子类）。由于引导类加载器涉及到虚拟机本地实现细节，开发者无法直接获取到启动类加载器的引用，所以不允许直接通过引用进行操作。
		2)扩展类加载器（extensions class loader）：它负责加载JRE的扩展目录，lib/ext或者由java.ext.dirs系统属性指定的目录中的JAR包的类。由Java语言实现，父类加载器为null。
		3)系统类加载器（system class loader）：被称为系统（也称为应用）类加载器，它负责在JVM启动时加载来自Java命令的-classpath选项、java.class.path系统属性，或者CLASSPATH换将变量所指定的JAR包和类路径。程序可以通过ClassLoader的静态方法getSystemClassLoader()来获取系统类加载器。如果没有特别指定，则用户自定义的类加载器都以此类加载器作为父加载器。由Java语言实现，父类加载器为ExtClassLoader。

自定义类加载器(custom class loader):
		通过继承ClassLoader基类来创建自己的类加载器

类加载器加载Class大致要经过如下8个步骤：

  		1. 检测此Class是否载入过，即在缓冲区中是否有此Class，如果有直接进入第8步，否则进入第2步
  		2. 如果没有父类加载器，则要么Parent是根类加载器，要么本身就是根类加载器，则跳到第4步，如果父类加载器存在，则进入第3步。
  		3. 请求使用父类加载器去载入目标类，如果载入成功则跳至第8步，否则接着执行第5步。
  		4. 请求使用根类加载器去载入目标类，如果载入成功则跳至第8步，否则跳至第7步。
  		5. 当前类加载器尝试寻找Class文件，如果找到则执行第6步，如果找不到则执行第7步。
  		6. 从文件中载入Class，成功后跳至第8步。
  		7. 抛出ClassNotFountException异常。
  		8. 返回对应的java.lang.Class对象。

**四、类加载机制**
1.JVM的类加载机制主要有如下3种。
全盘负责：所谓全盘负责，就是当一个类加载器负责加载某个Class时，该Class所依赖和引用其他Class也将由该类加载器负责载入，除非显示使用另外一个类加载器来载入。
双亲委派：所谓的双亲委派，则是先让父类加载器试图加载该Class，只有在父类加载器无法加载该类时才尝试从自己的类路径中加载该类。通俗的讲，就是某个特定的类加载器在接到加载类的请求时，首先将加载任务委托给父加载器，依次递归，如果父加载器可以完成类加载任务，就成功返回；只有父加载器无法完成此加载任务时，才自己去加载。
缓存机制：缓存机制将会保证所有加载过的Class都会被缓存，当程序中需要使用某个Class时，类加载器先从缓存区中搜寻该Class，只有当缓存区中不存在该Class对象时，系统才会读取该类对应的二进制数据，并将其转换成Class对象，存入缓冲区中。这就是为很么修改了Class后，必须重新启动JVM，程序所做的修改才会生效的原因。

关于双亲委派机制：
		双亲委派机制，其工作原理的是，如果一个类加载器收到了类加载请求，它并不会自己先去加载，而是把这个请求委托给父类的加载器去执行，如果父类加载器还存在其父类加载器，则进一步向上委托，依次递归，请求最终将到达顶层的启动类加载器，如果父类加载器可以完成类加载任务，就成功返回，倘若父类加载器无法完成此加载任务，子加载器才会尝试自己去加载，这就是双亲委派模式，即每个儿子都很懒，每次有活就丢给父亲去干，直到父亲说这件事我也干不了时，儿子自己才想办法去完成。

 双亲委派机制的优势：
		采用双亲委派模式的是好处是Java类随着它的类加载器一起具备了一种带有优先级的层次关系，通过这种层级关可以避免类的重复加载，当父亲已经加载了该类时，就没有必要子ClassLoader再加载一次。其次是考虑到安全因素，java核心api中定义类型不会被随意替换，假设通过网络传递一个名为java.lang.Integer的类，通过双亲委托模式传递到启动类加载器，而启动类加载器在核心Java API发现这个名字的类，发现该类已被加载，并不会重新加载网络传递的过来的java.lang.Integer，而直接返回已加载过的Integer.class，这样便可以防止核心API库被随意篡改

## 多线程

### synchronized和Lock的区别

参考【 https://www.cnblogs.com/ch-forever/p/10788674.html 】

1、原始构成不同
		synchronized是关键字，属于JVM层面， monitorenter(底层是通过monitor对象来完成，其实wait/notify等方法也依赖monitor对象只有在同步代码块和同步方法中才能调用wait/notify等方法) 
		Lock是具体的java接口，属于API层面
2、使用方法不同
		synchronized不需要手动释放锁，synchronized在代码执行完成后，系统会自动让线程释放对锁的占用
		ReentrantLock需要用户手动去释放锁，若没有主动释放锁，则有可能发生死锁现象，需要使用lock()方法和unlock()方法结合try finally语句块来完成
3、等待是否可中断
		synchronized不可中断，除非程序抛出异常或者正常运行完成
		ReentrantLock可以中断
			1) 设置超时方法tryLock(Long timeout, TimeUnit uint);
			2) lockInterruptibly()放入代码块中，调用interrupt()方法可中断
4、加锁是否公平
		synchronized是非公平锁
		ReentrantLock默认是非公平锁，可设置为公平锁
5、锁绑定多个条件condition
		synchronized没有，只能随机唤醒一个或者全部唤醒
		ReentrantLock用来分组唤醒需要唤醒的线程们，可以精确唤醒

详情可参考【 https://www.cnblogs.com/dolphin0520/p/3923167.html 】