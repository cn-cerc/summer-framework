#  版本历史

## 3.1.0 (2021-03-15)

1. 将旧的IHandle 拆分成 ISession、ISessionOwner。
2. 原有的 HandleDefault 拆分为了 TokenManageDefault（token创建和恢复），SessionDefault（数据链接、当前用户、设备信息、语言信息等）。
3. ISession 主要用于管理与数据库、缓存等第三方中间件的连接。
4. ISessionOwner 主要用于向框架中的登录、设备、权限、菜单、Token提供数据库连接。
5. 框架支持项目级、用户级的多语言支持，若用户切换了语言则以用户的多语言为主。详见 ClassResource 和 LanguageResource 的实现方式。
6. 将原有的 LocalConfig、ServerConfig合并成 ClassConfig，支持了外部用户配置、项目配置、框架配置的优先级实现。
7. 权限、设备、session、数据表等支持项目自定义实现统一放在 `cn.cerc.mis.custom` 包下，若不实现则使用框架自定义的实现方式。
8. 增加了对 Spring MVC 的菜单支持。
9. AbstractJspPage 拆分为 UIPage 和 JspPage，分别用于处理原始jsp文件和框架自定义生成页面。
---

## 3.2.0 (2021-03-18)

1. 将部分IPage替换为IView。
2. 增加IAppLanguage接口，解决R对象中对UserOptions硬引用限制，s_language 表由业务项目自己实现。
3. 增加ICorpInfo接口，以解决 MemoryBookInfo 和 MemoryBookRecord 获取帐套的问题。
---

## 3.2.1 (2021-03-18)

1. 在IForm中增加了getId与setId。
---

## 3.2.2 (2021-03-18)

1. 修复 Application 对象没有往自定义内部 session 赋值 sid 的问题，静态变量 TOKEN 统一指向 sid 字符串。
---

## 3.2.3 (2021-03-20)

1. 增加IOriginOwner接口。 
---
## 3.2.4 (2021-03-20)

1. summer-core 修复了config获取 application.properties getBoolean（）函数取值错误的问题。
---

## 3.2.4 (2021-03-20)

1. 修复task的handle没有取到值的问题
---