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

### Spring IOC容器

#### 容器简介

Spring 容器是Spring框架的核心，容器将创建对象，把他们连接在一起，配置他们，并管理他们的整个生命周期，从创建到销毁。Spring容器使用依赖注（DI）来管理组成一个应用程序的组件，这些对象被称为Spring Beans。
通过配置元数据提供的指令，容器知道对那些对象进行实例化，配置和组装。配置元数据可以通过XML，java注释或者java代码来表示。
Spring提供了两种不同类型的容器：
1、Spring BeanFactory 容器
它是最简单的容器，给DI提供了基本支持,它用org.springframework.beans.factory.BeanFactory接口来定义。BeanFactory 或者相关的接口，如 BeanFactoryAware，InitializingBean，DisposableBean，在 Spring 中仍然存在具有大量的与 Spring 整合的第三方框架的反向兼容性的目的。
2、Spring ApplicationContext 容器
该容器添加了更多的企业特定的功能，如从一个属性文件中解析文本信息的能力，发布应用程序事件给感兴趣的事件监听器的能力，由 org.springframework.context.ApplicationContext 接口定义。
ApplicationContext 容器包括 BeanFactory 容器的所有功能，所以通常建议超过 BeanFactory。BeanFactory 仍然可以用于轻量级的应用程序，如移动设备或基于 applet 的应用程序，其中它的数据量和速度是显著。

#### BeanFactory 容器

这是一个最简单的容器，它主要的功能是为依赖注入 （DI） 提供支持，这个容器接口在 org.springframework.beans.factory.BeanFactor 中被定义。 BeanFactory 和相关的接口，比如BeanFactoryAware、 DisposableBean、InitializingBean，仍旧保留在 Spring 中，主要目的是向后兼容已经存在的和那些 Spring 整合在一起的第三方框架。

在 Spring 中，有大量对 BeanFactory 接口的实现。其中，最常被使用的是 XmlBeanFactory 类。这个容器从一个 XML 文件中读取配置元数据，由这些元数据来生成一个被配置化的系统或者应用。

在资源宝贵的移动设备或者基于 applet 的应用当中， BeanFactory 会被优先选择。否则，一般使用的是 ApplicationContext，除非你有更好的理由选择 BeanFactory。

在主程序当中，我们需要注意以下两点：

- 第一步利用框架提供的 **XmlBeanFactory()** API 去生成工厂 bean 以及利用 **ClassPathResource()** API     去加载在路径 CLASSPATH 下可用的 bean 配置文件。**XmlBeanFactory()** API 负责创建并初始化所有的对象，即在配置文件中提到的 bean。
- 第二步利用第一步生成的 bean 工厂对象的 **getBean()** 方法得到所需要的 bean。 这个方法通过配置文件中的     bean ID 来返回一个真正的对象，该对象最后可以用于实际的对象。一旦得到这个对象，就可以利用这个对象来调用任何方法。

#### ApplicationContext 容器

Application Context 是 spring 中较高级的容器。和 BeanFactory 类似，它可以加载配置文件中定义的 bean，将所有的 bean 集中在一起，当有请求的时候分配 bean。 另外，它增加了企业所需要的功能，比如，从属性文件中解析文本信息和将事件传递给所指定的监听器。这个容器在 org.springframework.context.ApplicationContext interface 接口中定义。

ApplicationContext 包含 BeanFactory 所有的功能，一般情况下，相对于 BeanFactory，ApplicationContext 会更加优秀。当然，BeanFactory 仍可以在轻量级应用中使用，比如移动设备或者基于 applet 的应用程序。

最常被使用的 ApplicationContext 接口实现：

- **FileSystemXmlApplicationContext**：该容器从 XML 文件中加载已被定义的 bean。在这里，你需要提供给构造器 XML 文件的完整路径。
- **ClassPathXmlApplicationContext**：该容器从 XML 文件中加载已被定义的 bean。在这里，你不需要提供 XML 文件的完整路径，只需正确配置 CLASSPATH     环境变量即可，因为，容器会从 CLASSPATH 中搜索 bean 配置文件。
- **WebXmlApplicationContext**：该容器会在一个 web 应用程序的范围内加载在 XML 文件中已被定义的 bean。

在主程序当中，我们需要注意以下两点：

- 第一步生成工厂对象。加载完指定路径下 bean 配置文件后，利用框架提供的 FileSystemXmlApplicationContext API     去生成工厂 bean。FileSystemXmlApplicationContext 负责生成和初始化所有的对象，比如，所有在 XML bean 配置文件中的 bean。
- 第二步利用第一步生成的上下文中的 getBean() 方法得到所需要的 bean。 这个方法通过配置文件中的 bean ID 来返回一个真正的对象。一旦得到这个对象，就可以利用这个对象来调用任何方法。

#### Spring Bean定义

被称作 bean 的对象是构成应用程序的支柱也是由 Spring IoC 容器管理的。bean 是一个被实例化，组装，并通过 Spring IoC 容器所管理的对象。这些 bean 是由用容器提供的配置元数据创建的，例如，已经在先前章节看到的，在 XML 的表单中的 定义。

bean 定义包含称为**配置元数据**的信息，下述容器也需要知道配置元数据：

- 如何创建一个     bean
- bean 的生命周期的详细信息
- bean 的依赖关系

上述所有的配置元数据转换成一组构成每个 bean 定义的下列属性。

| **属性**                 | **描述**                                                     |
| ------------------------ | ------------------------------------------------------------ |
| class                    | 这个属性是强制性的，并且指定用来创建 bean 的 bean 类。       |
| name                     | 这个属性指定唯一的 bean 标识符。在基于 XML 的配置元数据中，你可以使用 ID 和/或 name 属性来指定 bean 标识符。 |
| scope                    | 这个属性指定由特定的 bean 定义创建的对象的作用域，它将会在 bean 作用域的章节中进行讨论。 |
| constructor-arg          | 它是用来注入依赖关系的，并会在接下来的章节中进行讨论。       |
| properties               | 它是用来注入依赖关系的，并会在接下来的章节中进行讨论。       |
| autowiring mode          | 它是用来注入依赖关系的，并会在接下来的章节中进行讨论。       |
| lazy-initialization mode | 延迟初始化的 bean 告诉 IoC 容器在它第一次被请求时，而不是在启动时去创建一个 bean 实例。 |
| initialization 方法      | 在 bean 的所有必需的属性被容器设置之后，调用回调方法。它将会在 bean 的生命周期章节中进行讨论。 |
| destruction 方法         | 当包含该 bean 的容器被销毁时，使用回调方法。它将会在 bean 的生命周期章节中进行讨论。 |

**Spring** **配置元数据**

Spring IoC 容器完全由实际编写的配置元数据的格式解耦。有下面三个重要的方法把配置元数据提供给 Spring 容器：

- 基于 XML 的配置文件。
- 基于注解的配置
- 基于 Java 的配置

#### Spring Bean的作用域

当在 Spring 中定义一个 bean 时，你必须声明该 bean 的作用域的选项。例如，为了强制 Spring 在每次需要时都产生一个新的 bean 实例，你应该声明 bean 的作用域的属性为 **prototype**。同理，如果你想让 Spring 在每次需要时都返回同一个bean实例，你应该声明 bean 的作用域的属性为 **singleton**。 

Spring 框架支持以下五个作用域，如果你使用 web-aware ApplicationContext 时，其中三个是可用的。 

| **作用域**     | **描述**                                                     |
| -------------- | ------------------------------------------------------------ |
| singleton      | 在spring IoC容器仅存在一个Bean实例，Bean以单例方式存在，默认值 |
| prototype      | 每次从容器中调用Bean时，都返回一个新的实例，即每次调用getBean()时，相当于执行newXxxBean() |
| request        | 每次HTTP请求都会创建一个新的Bean，该作用域仅适用于WebApplicationContext环境 |
| session        | 同一个HTTP Session共享一个Bean，不同Session使用不同的Bean，仅适用于WebApplicationContext环境 |
| global-session | 一般用于Portlet应用环境，该运用域仅适用于WebApplicationContext环境 |

本章将讨论前两个范围，当我们将讨论有关 web-aware Spring ApplicationContext 时，其余三个将被讨论。

**singleton** **作用域：**

当一个bean的作用域为Singleton，那么Spring IoC容器中只会存在一个共享的bean实例，并且所有对bean的请求，只要id与该bean定义相匹配，则只会返回bean的同一实例。

Singleton是单例类型，就是在创建起容器时就同时自动创建了一个bean的对象，不管你是否使用，他都存在了，每次获取到的对象都是同一个对象。注意，Singleton作用域是Spring中的缺省作用域。你可以在 bean 的配置文件中设置作用域的属性为 singleton

**prototype** **作用域**

当一个bean的作用域为Prototype，表示一个bean定义对应多个对象实例。Prototype作用域的bean会导致在每次对该bean请求（将其注入到另一个bean中，或者以程序的方式调用容器的getBean()方法）时都会创建一个新的bean实例。Prototype是原型类型，它在我们创建容器的时候并没有实例化，而是当我们获取bean的时候才会去创建一个对象，而且我们每次获取到的对象都不是同一个对象。根据经验，对有状态的bean应该使用prototype作用域，而对无状态的bean则应该使用singleton作用域。 

为了定义 prototype 作用域，你可以在 bean 的配置文件中设置作用域的属性为 prototype

#### spring Bean的生命周期 

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

#### BeanFactory 和 ApplicationContext的区别

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



