package cn.cerc.db.core;

public interface IAppConfig {
    // 各类应用配置代码
    public static final String PATH_FORMS = "application.pathForms";
    public static final String PATH_SERVICES = "application.pathServices";
    public static final String FORM_WELCOME = "application.formWelcome";
    public static final String FORM_DEFAULT = "application.formDefault";
    public static final String FORM_LOGOUT = "application.formLogout";
    public static final String FORM_VERIFY_DEVICE = "application.formVerifyDevice";
    public static final String JSPFILE_LOGIN = "application.jspLoginFile";
//  public static final String FORM_ID = "formId";

    // 未登录时的首页
    String getWelcomePage();

    // 登录后的首页
    String getDefaultPage();

    // 需要认证时的密码页
    String getJspLoginFile();

    // 需要检验新设备的页面
    String getVerifyDevicePage();

    // 系统退出时的页面
    String getLogoutPage();

    // 返回 form 的路径，默认为 forms
    String getFormsPath();

    // 返回 form 的路径，默认为 services
    String getServicesPath();

    @Deprecated
    default String getFormWelcome() {
        return getWelcomePage();
    }

    @Deprecated
    default String getFormDefault() {
        return getDefaultPage();
    }

}
