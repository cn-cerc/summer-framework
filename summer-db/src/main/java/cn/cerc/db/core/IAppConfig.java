package cn.cerc.db.core;

public interface IAppConfig {

    // 未登录时的首页
    String getFormWelcome();

    // 登录后的首页
    String getFormDefault();

    // 需要认证时的密码页
    String getJspLoginFile();

    // 需要检验新设备的页面
    String getFormVerifyDevice();

    // 系统退出时的页面
    String getFormLogout();

    // 返回 form 的路径，默认为 forms
    String getPathForms();

    // 返回 form 的路径，默认为 services
    String getPathServices();

}
