# summer-framework

## 框架简介

包括了 summer-framework 父级pom文件，为框架及应用提供 jar 统一版本。

1. summer开源框架主要包含summer-model、summer-controller、summer-ui、summer-local 4个大模组。
2. 使用业界成熟的MVC架构，以spring为基础，提供快速开发服务的基础框架服务。
3. 框架github地址：https://github.com/cn-cerc/summer-framework
4. 框架gitee地址：   https://gitee.com/mimrc/summer-framework

## 开发配置

若开发人员使用框架，建议直接将 summer-framework 克隆到自己本地电脑进行打包，以获取最佳的使用体验，同时更新和修改框架也方便。

1. 将 `settings.xml` 文件拷贝到系统用户根目录 `.m2` 文件夹下，可加速jar包的下载速度。

2. 将 summer-framework 的所有模组代码下载到本地，执行 `summer-clone.sh` 。

3. 将框架打包到本地的 maven 仓库，执行 `summer-install.sh`。

4. 一键切换到develop分支，执行 `switch-to-deve.sh`。

5. 一键切换到beta分支，执行 `switch-to-beta.sh`。

## 私服引用

```xml
<repositories>
    <repository>
        <id>nexus</id>
        <url>https://nexus.diteng.site/nexus/content/groups/public</url>
    </repository>
</repositories>
```

## 使用范例

详细请见 summer-sample

github: https://github.com/cn-cerc/summer-sample

gitee:   https://gitee.com/mimrc/summer-sample