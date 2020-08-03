# Activiti之统一身份管理

[钝之](https://me.csdn.net/Jessechanrui) 2019-03-11 23:28:14 
身份相关的表：

> act_id_user:用户表
> act_id_info:用户信息表
> act_id_group:组（或角色）
> act_id_memership:用户与组的关系 ，查询任务获选人需关联的表

   Activiti的用户与组过于简单，且每个项目基本上已有一套完整的用户管理模块。使用Activiti时同时管理两套用户管理机制，在成本和易用性上来说不可取。为此提供几个解决方案。

### 1. 通过引擎接口同步数据

  非入侵式的同步方式，类似于引擎中的监听，当现有身份模块的用户数据变更时调用Activiti引擎的IdentityService接口的对应方法同步操作。但是在同步数据时应做好数据的校验工作，如添加、修改用户和和组前先检查对象是否存在，以防止引擎接口抛出异常。
  特点是不破坏引擎表结构、面向接口编程。引擎内部的SQL关联插叙可正常工作。

### 2. 自定义Session工厂

  Activiti的每一张表都有一个对应的实体管理器，在引擎初始化时会初始化所有表的实体管理类，每一个实体类都有一个对应的实体管理类XxxEntityManager及实体管理工厂类XxxEntityManagerFactory。实体管理工厂类实现SessionFactory接口。

  引擎允许我们覆盖内置的实体管理器使用自定义的实体管理器替代默认实现类，以替换与引擎相关的实体管理类，把自定义的实体管理类注入引擎中，自定义实体管理类只要实现引擎的接口即可。

  配置如下：

```xml
	<!-- 自定义用户模块 -->
	<bean id="customUserEntityManager" class="cn.com.agree.aweb.springmvc.activiti.CustomUserEntityManager">
	</bean>
	<bean id="customGroupEntityManager" class="cn.com.agree.aweb.springmvc.activiti.CustomGroupEntityManager">
	</bean>
12345
```

  在引擎配置中添加该属性

```xml
<property name="customSessionFactories">
	         <list>
	              <bean class="cn.com.agree.aweb.springmvc.activiti.CustomUserEntityManagerFactory"></bean>
	              <bean class="cn.com.agree.aweb.springmvc.activiti.CustomGroupEntityManagerFactory"></bean>
	         </list>
		</property> 
```

  自定义用户管理：继承UserEntityManager，重写方法（调用自身项目的管理模块接口，实现相应功能）

```java
package cn.com.agree.aweb.springmvc.activiti;

import java.util.List;
import java.util.Map;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.Picture;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.UserQueryImpl;
import org.activiti.engine.impl.persistence.entity.IdentityInfoEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.impl.persistence.entity.UserEntityManager;
import org.springframework.beans.factory.annotation.Autowired;

import cn.com.agree.aweb.exception.DBSupportException;
import cn.com.agree.aweb.hibernate.IDbSupport;
import cn.com.agree.aweb.hibernate.entity.UserVO;

/**
 * @Description: 自定义用户管理
 * @author chenrui  chenrui@agree.com.cn
 * @date 2016年8月2日
 *
 */
public class CustomUserEntityManager extends UserEntityManager{
	@Autowired
	private IDbSupport dbSupport;

    @Override
	public User findUserById(String userId) {
		User userEntity = new UserEntity();
		try {
			UserVO userVO = queryUserByUsername(userId);
			userEntity.setId(userVO.getUserId());
			userEntity.setFirstName(userVO.getNickname());
			userEntity.setLastName(userVO.getNickname());
			userEntity.setPassword(userVO.getPassword());
			userEntity.setEmail(userVO.getMailbox());
		} catch (DBSupportException e) {
			e.printStackTrace();
		}
		return userEntity;
	}
	
	
	
	@Override
	public User createNewUser(String userId) {
		// TODO Auto-generated method stub
		User user = new UserEntity();
		return user;
	}




	@Override
	public void insertUser(User user) {
		// TODO Auto-generated method stub
		super.insertUser(user);
	}




	@Override
	public void updateUser(User updatedUser) {
		// TODO Auto-generated method stub
		super.updateUser(updatedUser);
	}

	@Override
	public void deleteUser(String userId) {
		// TODO Auto-generated method stub
		super.deleteUser(userId);
	}




	@Override
	public List<User> findUserByQueryCriteria(UserQueryImpl query, Page page) {
		// TODO Auto-generated method stub
		return super.findUserByQueryCriteria(query, page);
	}




	@Override
	public long findUserCountByQueryCriteria(UserQueryImpl query) {
		// TODO Auto-generated method stub
		return super.findUserCountByQueryCriteria(query);
	}




	@Override
	public List<Group> findGroupsByUser(String userId) {
		// TODO Auto-generated method stub
		return super.findGroupsByUser(userId);
	}




	@Override
	public UserQuery createNewUserQuery() {
		// TODO Auto-generated method stub
		return super.createNewUserQuery();
	}




	@Override
	public IdentityInfoEntity findUserInfoByUserIdAndKey(String userId,
			String key) {
		// TODO Auto-generated method stub
		return super.findUserInfoByUserIdAndKey(userId, key);
	}




	@Override
	public List<String> findUserInfoKeysByUserIdAndType(String userId,
			String type) {
		// TODO Auto-generated method stub
		return super.findUserInfoKeysByUserIdAndType(userId, type);
	}




	@Override
	public Boolean checkPassword(String userId, String password) {
		// TODO Auto-generated method stub
		return super.checkPassword(userId, password);
	}




	@Override
	public List<User> findPotentialStarterUsers(String proceDefId) {
		// TODO Auto-generated method stub
		return super.findPotentialStarterUsers(proceDefId);
	}




	@Override
	public List<User> findUsersByNativeQuery(Map<String, Object> parameterMap,
			int firstResult, int maxResults) {
		// TODO Auto-generated method stub
		return super.findUsersByNativeQuery(parameterMap, firstResult, maxResults);
	}




	@Override
	public long findUserCountByNativeQuery(Map<String, Object> parameterMap) {
		// TODO Auto-generated method stub
		return super.findUserCountByNativeQuery(parameterMap);
	}




	@Override
	public boolean isNewUser(User user) {
		// TODO Auto-generated method stub
		return super.isNewUser(user);
	}




	@Override
	public Picture getUserPicture(String userId) {
		// TODO Auto-generated method stub
		return super.getUserPicture(userId);
	}




	@Override
	public void setUserPicture(String userId, Picture picture) {
		// TODO Auto-generated method stub
		super.setUserPicture(userId, picture);
	}




	/**
	 * @param username
	 * @return
	 * @throws DBSupportException
	 */
	@SuppressWarnings("unchecked")
	public UserVO queryUserByUsername(String username) throws DBSupportException {
        List<UserVO> userList = (List<UserVO>) dbSupport.queryDataByClass(UserVO.class, 
                new String[] {"username"}, 
                new String[] {username});

        if (userList.size() == 1) {
            return userList.get(0);
        } 

        return null;
    }
	
	
}

```

  自定义Session工厂集合中，实现对应的session类型

```java
package cn.com.agree.aweb.springmvc.activiti;

import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.UserIdentityManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 自定义用户
 * @author chenrui  chenrui@agree.com.cn
 * @date 2016年8月2日
 *
 */
public class CustomUserEntityManagerFactory implements SessionFactory{
	 
	@Autowired
	private CustomUserEntityManager customUserEntityManager;
	
	@Override
	public Class<?> getSessionType() {
		return UserIdentityManager.class;
	}

	@Override
	public Session openSession() {
		return  customUserEntityManager;
	}



}

1234567891011121314151617181920212223242526272829303132
```

  自定义用户组同理。如此即可使用IdentityService使用项目的用户管理模块。

### 3. 用视图代替物理表

  将引擎的物理表删除，换成与物理表同名的视图，制药保证视图的表结构与原来物理表的表结构及字段类型保持一致即可。

  **注意**：删除引擎身份模块的物理表后启动会报错，提示缺少身份模块的表。

  **解决**：在初始化引擎时不再检查ACT_ID_*表是否存在，禁用身份模块的功能，在引擎配置中添加属性配置

```xml
    <property name="dbIdentityUsed" value="false"/>
1
```

### 4. 集成LDAP

  **LDAP**：轻量目录访问协议，用来管理地址簿或人员与资源的关系。（应用场景：有多个业务系统时使用LDAP统一管理用户资源，各个系统不需单独维护而是通过LDAP协议获取所需用户数据）。

```xml
<!-- 嵌入LDAP服务器（从classpath环境中读取） -->
    <security:ldap-server ldif="classpath:chapter19/users.ldif" root="o=aia"/>
12
```

  在工作流引擎配置中添加属性

```xml
 <!-- LDAP -->
        <property name="configurators">
            <list>
                <bean class="org.activiti.ldap.LDAPConfigurator">

                    <!-- Server connection params -->
                    <property name="server" value="ldap://localhost" />
                    <property name="port" value="33389" />
                    <property name="user" value="uid=admin, ou=users, o=aia" />
                    <property name="password" value="pass" />
                    <property name="initialContextFactory" value="com.sun.jndi.ldap.LdapCtxFactory" />
                    <property name="securityAuthentication" value="simple" />

                    <!-- Query params -->
                    <property name="baseDn" value="o=aia" />
                    <property name="userBaseDn" value="ou=users,o=aia" />
                    <property name="groupBaseDn" value="ou=groups,o=aia" />
                    <property name="queryUserByUserId" value="(&amp;(objectClass=inetOrgPerson)(uid={0}))" />
                    <property name="queryUserByFullNameLike" value="(&amp;(objectClass=inetOrgPerson)(|({0}=*{1}*)({2}=*{3}*)))" />
                    <property name="queryGroupsForUser" value="(&amp;(objectClass=groupOfUniqueNames)(uniqueMember={0}))" />

                    <!-- Attribute config -->
                    <property name="userIdAttribute" value="uid" />
                    <property name="userFirstNameAttribute" value="cn" />
                    <property name="userLastNameAttribute" value="sn" />

                    <property name="groupIdAttribute" value="uid" />
                    <property name="groupNameAttribute" value="cn" />

                </bean>
            </list>
        </property>
```