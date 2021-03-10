package cn.cerc.mis.config;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.cerc.core.ClassConfig;
import cn.cerc.db.core.IAppConfig;
import cn.cerc.mis.core.Application;
import cn.cerc.mvc.SummerMVC;

@Component
@Deprecated
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AppConfigDefault implements IAppConfig {
    private static final ClassConfig config = new ClassConfig(AppConfigDefault.class, SummerMVC.ID);

    @Deprecated
    @Override
    public String getPathForms() {
        return config.getString(Application.PATH_FORMS, "forms");
    }

    @Deprecated
    @Override
    public String getPathServices() {
        return config.getString(Application.PATH_SERVICES, "services");
    }

    /**
     * @return 返回默认的欢迎页
     */
    @Deprecated
    @Override
    public String getFormWelcome() {
        return config.getString(Application.FORM_WELCOME, "welcome");
    }

    /**
     * @return 返回默认的主菜单
     */
    @Deprecated
    @Override
    public String getFormDefault() {
        return config.getString(Application.FORM_DEFAULT, "default");
    }

    /**
     * @return 退出系统确认画面
     */
    @Deprecated
    @Override
    public String getFormLogout() {
        return config.getString(Application.FORM_LOGOUT, "logout");
    }

    /**
     * @return 当前设备第一次登录时需要验证设备
     */
    @Deprecated
    @Override
    public String getFormVerifyDevice() {
        return config.getString(Application.FORM_VERIFY_DEVICE, "VerifyDevice");
    }

    /**
     * @return 在需要用户输入帐号、密码进行登录时的显示
     */
    @Deprecated
    @Override
    public String getJspLoginFile() {
        return config.getString(Application.JSPFILE_LOGIN, "common/FrmLogin.jsp");
    }

    @Deprecated
    @Override
    public String getProperty(String key, String def) {
        return config.getString(key, def);
    }

}
