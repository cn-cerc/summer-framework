# summer-framework

包括了 summer-framework 总pom文件，为框架及应用提供 jar 统一版本。

## 开发配置

1. 将 `settings.xml` 文件拷贝到系统用户根目录 `.m2` 文件夹下，可加速jar包的下载速度。

2. 将 summer-framework 的所有模组代码下载到本地，执行 `summer-clone.sh` 。

3. 将框架打包到本地的 maven 仓库，执行 `summer-install.sh`。

4. 一键切换到develop分支，执行 `switch-to-deve.sh`。

5. 一键切换到beta分支，执行 `switch-to-beta.sh`。
