package cn.cerc.mis.config;

import org.springframework.stereotype.Component;

import cn.cerc.core.ClassConfig;
import cn.cerc.db.core.IAppConfig;
import cn.cerc.mis.SummerMIS;

@Component
public class AppConfigDefault implements IAppConfig {
    private static final ClassConfig config = new ClassConfig(AppConfigDefault.class, SummerMIS.ID);

    @Override
    public String getFormsPath() {
        return config.getString(IAppConfig.PATH_FORMS, "forms");
    }

    @Override
    public String getServicesPath() {
        return config.getString(IAppConfig.PATH_SERVICES, "services");
    }

    /**
     * @return 返回默认的欢迎页
     */
    @Override
    public String getWelcomePage() {
        return config.getString(IAppConfig.FORM_WELCOME, "welcome");
    }

    /**
     * @return 返回默认的主菜单
     */
    @Override
    public String getDefaultPage() {
        return config.getString(IAppConfig.FORM_DEFAULT, "default");
    }

    /**
     * @return 退出系统确认画面
     */
    @Override
    public String getLogoutPage() {
        return config.getString(IAppConfig.FORM_LOGOUT, "logout");
    }

    /**
     * @return 当前设备第一次登录时需要验证设备
     */
    @Override
    public String getVerifyDevicePage() {
        return config.getString(IAppConfig.FORM_VERIFY_DEVICE, "VerifyDevice");
    }

    /**
     * @return 在需要用户输入帐号、密码进行登录时的显示
     */
    @Deprecated
    @Override
    public String getJspLoginFile() {
        return config.getString(IAppConfig.JSPFILE_LOGIN, "common/FrmLogin.jsp");
    }

}
