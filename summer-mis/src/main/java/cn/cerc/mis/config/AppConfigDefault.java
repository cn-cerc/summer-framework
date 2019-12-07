package cn.cerc.mis.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.cerc.db.core.IAppConfig;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AppConfigDefault implements IAppConfig {
    private Map<String, String> params = new HashMap<>();

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    @Override
    public String getPathForms() {
        return getParam("pathForms", "forms");
    }

    @Override
    public String getPathServices() {
        return getParam("pathServices", "services");
    }

    /**
     * 
     * @return 返回默认的欢迎页
     */
    @Override
    public String getFormWelcome() {
        return getParam("formWelcome", "welcome");
    }

    /**
     * 
     * @return 返回默认的主菜单
     */
    @Override
    public String getFormDefault() {
        return getParam("formDefault", "default");
    }

    /**
     *
     * @return 退出系统确认画面
     */
    @Override
    public String getFormLogout() {
        return getParam("formLogout", "logout");
    }

    /**
     * 
     * @return 当前设备第一次登录时需要验证设备
     */
    @Override
    public String getFormVerifyDevice() {
        return getParam("formVerifyDevice", "VerifyDevice");
    }

    /**
     * 
     * @return 在需要用户输入帐号、密码进行登录时的显示
     */
    @Override
    public String getJspLoginFile() {
        return getParam("jspLoginFile", "common/FrmLogin.jsp");
    }

    public String getParam(String key, String def) {
        String val = params.get(key);
        return val != null ? val : def;
    }

    @Override
    public String getProperty(String key, String def) {
        String val = params.get(key);
        return val != null ? val : def;
    }

    @Override
    public String getProperty(String key) {
        return params.get(key);
    }
}
