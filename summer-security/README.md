# summer-security

欢迎使用聚安服务云平台!

聚安云服务致力于为互联网应用提供安全保障服务，降低每一个系统在应用层安全上大费周章。

当前主要提供的服务有

* 用户真实身份保障服务，防止黑客冒用身份登录
* 提供简讯发送服务，智能选择最快到达的简讯服务商
* 提供动态DNS服务，就近安排用户登录

以上服务，只要求登记手机号即可开通并使用

# application.properties

```
# 应用钥匙
jayun.appKey=test

# 应用密钥
jayun.appSecret=123456

# 停止调用
jayun.stop=1
```
# pom.xml

```xml
<dependency>
    <groupId>cn.cerc</groupId>
    <artifactId>summer-security</artifactId>
    <version>1.4.3</version>
</dependency>
```

