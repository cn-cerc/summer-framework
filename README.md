# summer-framework

[![Build Status](https://travis-ci.org/cn-cerc/summer-framework.svg)](https://travis-ci.org/cn-cerc/summer-framework)

## 简介

1. summer开源框架包含summer-bean,summer-core,summer-db,summer-mis.使用业界成熟的MVC架构，以spring为基础，提供快速开发服务的基础框架服务。
2. 框架地址：https://github.com/cn-cerc/summer-framework
3. 其中核心部分summer-bean对象主要有IForm与IService，二者结合可低成本地实现微服务架构，同时保障系统功能弹性与性能弹性：

> **IForm**，定位于页面控制器，用于接收web输入，以及输出IPage接口。 其中IPage实现有：JspPage、JsonPage、RedirectPage等，可自由扩充。实际编写时，可直接继承AbstractForm后快速实现具体的页面控制器。

> **IService**，定位于业务逻辑，用于接收web输出，以及输出IStatus与DataSet-JSON，并可通过包装类，转化为其它格式如xml的输出，此项与IForm的差别在于：

IForm有提供对getRequest().getSession()的访问，可使用HttpSession。

IService有提供 RESTful 接口，可提供第三方访问。 实际使用时，IForm会调用IService，而IService既对内提供业务服务，也对外提供业务服务。更多的详细介绍，敬请期待...

欢迎大家反馈更多的建议与意见，也欢迎其它业内人士，对此免费框架进行协同改进！

## 版本历史

[查看](Version.md)

# 模块

[summer-db](summer-db) 数据库操作服务

[summer-mis](summer-mis) 定位于页面组件，默认静态文件路径 forms

# 私服

## 在pom.xml中引用

```xml
<repositories>
    <repository>
        <id>nexus</id>
        <url>https://nexus.diteng.site/nexus/content/groups/public</url>
    </repository>
</repositories>
```

## 在setting.xml中引用

```xml
<mirror>
    <id>nexus-maven</id>
    <mirrorOf>central</mirrorOf>
    <url>https://nexus.diteng.site/nexus/content/groups/public</url>
</mirror>
```

## 开发人员使用

若开发人员使用框架，建议直接将 summer-framework 克隆到自己本地电脑进行打包，以获取最佳的使用体验，同时更新和修改框架也方便。

```bash
git clone https://github.com/cn-cerc/summer-framework.git

cd summer-framework

sh build.sh
```
