# summer-framework

## 框架简介

summer开源框架包含summer-bean,summer-core,summer-db,summer-mis.使用业界成熟的MVC架构，以spring为基础，提供快速开发服务的基础框架服务。

框架地址：https://github.com/cn-cerc 安全服务：[聚安](https://www.jayun.site)

其中核心部分summer-bean对象主要有IForm与IService，二者结合可低成本地实现微服务架构，同时保障系统功能弹性与性能弹性：

**IForm**，定位于页面控制器，用于接收web输入，以及输出IPage接口。 其中IPage实现有：JspPage、JsonPage、RedirectPage等，可自由扩充。实际编写时，可直接继承AbstractForm后快速实现具体的页面控制器。

**IService**，定位于业务逻辑，用于接收web输出，以及输出IStatus与DataSet-JSON，并可透过包装类，转化为其它格式如xml的输出，此项与IForm的差别在于：

IForm有提供对getRequest().getSession()的访问，可使用HttpSession。

IService有提供RESTful接口，可提供第三方访问。 实际使用时，IForm会调用IService，而IService既对内提供业务服务，也对外提供业务服务。更多的详细介绍，敬请期待...

欢迎大家反馈更多的建议与意见，也欢迎其它业内人士，对此免费框架进行协同改进！