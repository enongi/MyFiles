# Spring源码--BeanFactory和FactoryBean区别

2018-07-10 21:18:57  收起
 分类专栏： [# ---Spring源码](https://blog.csdn.net/yhl_jxy/category_7793958.html) 

版权声明：本文为博主原创文章，遵循[ CC 4.0 BY-SA ](http://creativecommons.org/licenses/by-sa/4.0/)版权协议，转载请附上原文出处链接和本声明。本文链接：https://blog.csdn.net/yhl_jxy/article/details/80991201

# **一 BeanFactory和FactoryBean概念**

  BeanFactory和FactoryBean在Spring中是两个使用频率很高的类，它们在拼写上非常相似，

需要注意的是，两者除了名字看上去像一点外，从实质上来说是一个没有多大关系的东西。

**BeanFactory是一个IOC容器或Bean对象工厂；**

**FactoryBean是一个Bean；**

在Spring中有两种Bean，一种是普通Bean，另一种就是像FactoryBean这样的**工厂Bean**，

无论是那种Bean，都是由IOC容器来管理的。

FactoryBean可以说为IOC容器中Bean的实现提供了更加灵活的方式，FactoryBean在IOC容器的基础上给Bean的实现

加上了一个简单工厂模式和装饰模式，我们可以在getObject()方法中灵活配置。

# 二 BeanFactory和FactoryBean深入源码

## **1、BeanFactory**

  BeanFactory是IOC最基本的容器，负责生产和管理bean，它为其他具体的IOC容器提供了最基本的规范，

例如DefaultListableBeanFactory，XmlBeanFactory，ApplicationContext 等具体的容器都是实现了BeanFactory，

再在其基础之上附加了其他的功能。

**BeanFactory源码：**

```java
package org.springframework.beans.factory;
import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
public interface BeanFactory {
	String FACTORY_BEAN_PREFIX = "&";
	Object getBean(String name) throws BeansException;
	<T> T getBean(String name, Class<T> requiredType) throws BeansException;
	<T> T getBean(Class<T> requiredType) throws BeansException;
	Object getBean(String name, Object... args) throws BeansException;
	<T> T getBean(Class<T> requiredType, Object... args) throws BeansException;
	boolean containsBean(String name);
	boolean isSingleton(String name) throws NoSuchBeanDefinitionException;
	boolean isPrototype(String name) throws NoSuchBeanDefinitionException;
	boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException;
	boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException;
	Class<?> getType(String name) throws NoSuchBeanDefinitionException;
	String[] getAliases(String name);
}
```

## **2、FactoryBean**

  FactoryBean是一个接口，当在IOC容器中的Bean实现了FactoryBean后，通过getBean(String BeanName)获取

到的Bean对象并不是FactoryBean的实现类对象，而是这个实现类中的getObject()方法返回的对象。

要想获取FactoryBean的实现类，就要getBean(&BeanName)，在BeanName之前加上&。

**FactoryBean源码：**

```java
package org.springframework.beans.factory;
public interface FactoryBean<T> {
	// 返回由FactoryBean创建的Bean的实例
	T getObject() throws Exception;
	// 返回FactoryBean创建的Bean的类型
	Class<?> getObjectType();
	// 确定由FactoryBean创建的Bean的作用域是singleton还是prototype
	boolean isSingleton();
}
```

## **3、实例分析**

**AppleBean：**

```java
package com.lanhuigu.spring.bean;
import org.springframework.stereotype.Component;
public class AppleBean {
}
```

**AppleFactoryBean：**

```java
package com.lanhuigu.spring.bean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;
 
@Component
public class AppleFactoryBean implements FactoryBean{
 
    @Override
    public Object getObject() throws Exception {
        return new AppleBean();
    }
 
    @Override
    public Class<?> getObjectType() {
        return AppleBean.class;
    }
 
    @Override
    public boolean isSingleton() {
        return false;
    }
}
```

**Spring配置类：**

```java
package com.lanhuigu.spring.bean;
 
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
 
@Configuration
@ComponentScan
public class AppConfiguration {
 
}
```

**启动类：**

```java
package com.lanhuigu.spring.bean;
 
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
 
public class StartTest {
    public static void main(String[] args){
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfiguration.class);
        // 得到的是apple
        System.out.println(context.getBean("appleFactoryBean"));
        // 得到的是apple工厂
        System.out.println(context.getBean("&appleFactoryBean"));
    }
}
```

**运行结果：**

com.lanhuigu.spring.bean.AppleBean@679b62af
com.lanhuigu.spring.bean.AppleFactoryBean@5cdd8682

从结果可以看出第一个打印出来的是在getObject()中new的AppleBean对象，是一个普通的Bean，

第二个通过加上&获取的是实现了FactoryBean接口的AppleFactoryBean对象，是一个工厂Bean。

# **三 总结**

1、BeanFactory是一个IOC基础容器。

2、FactoryBean是一个Bean，不是一个普通Bean，是一个工厂Bean。

3、FactoryBean实现与工厂模式和装饰模式类似。

4、通过转义符&来区分获取FactoryBean产生的对象和FactoryBean对象本身（FactoryBean实现类）。