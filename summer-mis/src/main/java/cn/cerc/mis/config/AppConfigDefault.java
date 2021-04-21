package cn.cerc.mis.config;

import org.springframework.stereotype.Component;

import cn.cerc.core.ClassConfig;
import cn.cerc.db.core.IAppConfig;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.core.Application;

@Component
public class AppConfigDefault implements IAppConfig {
    private static final ClassConfig config = new ClassConfig(AppConfigDefault.class, SummerMIS.ID);

    @Override
    public String getPathForms() {
        return config.getString(Application.PATH_FORMS, "forms");
    }

    @Override
    public String getPathServices() {
        return config.getString(Application.PATH_SERVICES, "services");
    }

    /**
     * @return 返回默认的欢迎页
     */
    @Override
    public String getFormWelcome() {
        return config.getString(Application.FORM_WELCOME, "welcome");
    }

    /**
     * @return 返回默认的主菜单
     */
    @Override
    public String getFormDefault() {
        return config.getString(Application.FORM_DEFAULT, "default");
    }

    /**
     * @return 退出系统确认画面
     */
    @Override
    public String getFormLogout() {
        return config.getString(Application.FORM_LOGOUT, "logout");
    }

    /**
     * @return 当前设备第一次登录时需要验证设备
     */
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

}
