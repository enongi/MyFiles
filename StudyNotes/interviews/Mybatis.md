## Mybatis

### Mybatis和Hibernate的本质区别和使用场景

Hibernate是一个标准化的ORM框架，入门的门槛较高，不需要程序写sql，语句自动生成，sql的优化修改比较困难
应用场景：适用于中小企业需求变化不多的场景，比如后台管理系统，ERP,ORM,OA.

Mybatis专注于sql本身，程序员要自己写sql语句，sql的修改与优化比较方便，是一个不完全的ORM框架，虽然程序员自己写sql，但它自身也可以实现映射
应用场景：适用于需求变化较多的项目，如互联网项目等
