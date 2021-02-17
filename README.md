# ktadmin

使用kotlin语言基于springboot框架的后台管理系统

使用kotlin语言开发，主要框架是Spring Boot

* 数据访问使用jpa，代码中主要使用jpa的扩展接口与hql，所以可通过配置切换数据库
* 现在系统中默认使用mysql，数据可能过测试用例中MainTests中的initData()方法来生成默认数据
* 认证使用Spring Security、Jwt。
* 主要实现了部门、用户、权限（功能、数据）的配置
* web端基于vue-element-admin开发，地址 https://github.com/maioria/ktadmin-element-web
## 在线体验

演示地址：
https://maioria.github.io/ktadmin
