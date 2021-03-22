package cn.cerc.mis.security.sapi;

import javax.servlet.http.HttpServletRequest;

public class JayunConfig {
    public static final String sms_template_user_register = "sms_template_user_register";
    public static final String sms_template_security_check = "sms_template_security_check";
    public static final String sms_template_send_voice = "sms_template_send_voice";
    private HttpServletRequest request;
    private String message;
    private String appKey;
    private String appSecret;

    public JayunConfig(HttpServletRequest request) {
        this.request = request;
    }

    public boolean update_sms_template_user_register(String templateId) {
        return updateBookOption(sms_template_user_register, templateId);
    }

    public boolean update_sms_template_security_check(String templateId) {
        return updateBookOption(sms_template_security_check, templateId);
    }

    public boolean update_sms_template_send_voice(String templateId) {
        return updateBookOption(sms_template_send_voice, templateId);
    }

    private boolean updateBookOption(String optionKey, String optionValue) {
        JayunAPI api = new JayunAPI(request);
        api.put("appKey", appKey);
        api.put("appSecret", appSecret);
        api.put("optionKey", optionKey);
        api.put("optionValue", optionValue);
        api.post("config.bookOption");
        this.setMessage(api.getMessage());
        return api.isResult();
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
