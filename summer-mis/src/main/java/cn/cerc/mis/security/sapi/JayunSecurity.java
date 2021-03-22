package cn.cerc.mis.security.sapi;

import javax.servlet.http.HttpServletRequest;

public class JayunSecurity {
    private static final String deviceId = "deviceId";
    private static final String securityCode = "securityCode";
    /**
     * 发送模式: 默认为手机简讯，可支持语音发送以及其它方式
     */
    private SendMode sendMode = SendMode.SMS;
    private HttpServletRequest request;
    private String message;
    private Object data;

    public JayunSecurity(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * 查询应用用户是否绑定聚安帐号
     *
     * @param user 应用用户帐号
     * @return true 已绑定, false 未绑定
     */
    public boolean isBind(String user) {
        JayunAPI api = new JayunAPI(request);
        api.put("ip", api.getRemoteIP());
        api.put("user", user);
        api.post("security.isBind");
        this.setMessage(api.getMessage());
        return api.isResult();
    }

    /**
     * 应用用户解除绑定聚安
     *
     * @param user 应用用户帐号
     * @return true 解绑成功, false 解绑失败
     */
    public boolean unBind(String user) {
        JayunAPI api = new JayunAPI(request);
        api.put("ip", api.getRemoteIP());
        api.put("user", user);
        api.post("security.unBind");
        this.setMessage(api.getMessage());
        return api.isResult();
    }

    /**
     * 向聚安云平台注册用户资料，以及所关联手机号讯息，后续会增加更多的讯息用于登记
     *
     * @param user   应用的用户账号，并非聚安云的帐号
     * @param mobile 手机号
     * @return true:成功，若失败可用getMessage取得错误信息
     */
    public boolean register(String user, String mobile) {
        JayunAPI api = new JayunAPI(request);
        api.put("ip", api.getRemoteIP());
        api.put("user", user);
        api.put("mobile", mobile);
        api.post("security.register");
        this.setMessage(api.getMessage());
        return api.isResult();
    }

    /**
     * 检测用户当前使用的IP以及设备是否是安全，不安全的原因可能有：未认证的IP、设备，或未许可的时间段
     *
     * @param user 应用的用户账号，并非聚安云的帐号r
     * @return true:成功，若失败可用getMessage取得错误信息
     */
    public boolean isSecurity(String user) {
        JayunAPI api = new JayunAPI(request);
        api.put("ip", api.getRemoteIP());
        api.put("user", user);
        api.put("deviceId", getDeviceId());
        api.post("security.isSecurity");
        this.setMessage(api.getMessage());
        return api.isResult();
    }

    /**
     * 发送验证码
     *
     * @param user      应用的用户账号，并非聚安云的帐号
     * @param sendVoice true: 发送语音验证码;false:发送普通短信验证码
     * @return true 成功，若失败可用getMessage取得错误信息
     */
    @Deprecated
    public boolean requestVerify(String user, boolean sendVoice) {
        if (sendVoice)
            this.sendMode = SendMode.VOICE;
        return requestVerify(user);
    }

    /**
     * 发送验证码
     *
     * @param user 应用的用户账号，并非聚安云的帐号
     * @return true 成功，若失败可用getMessage取得错误信息
     */
    public boolean requestVerify(String user) {
        JayunAPI api = new JayunAPI(request);
        api.put("ip", api.getRemoteIP());
        api.put("user", user);
        api.put("deviceId", getDeviceId());
        api.put("sendMode", sendMode.name().toLowerCase());
        if (sendMode == SendMode.VOICE)
            api.put("sendVoice", "true");
        api.post("security.requestVerify");
        this.setMessage(api.getMessage());
        return api.isResult();
    }

    /**
     * 检测验证码
     *
     * @param user       应用的用户账号，并非聚安云的帐号
     * @param verifyCode 验证码
     * @return true:成功，若失败可用getMessage取得错误信息
     */
    public boolean checkVerify(String user, String verifyCode) {
        JayunAPI api = new JayunAPI(request);
        api.put("ip", api.getRemoteIP());
        api.put("user", user);
        api.put("verifyCode", verifyCode);
        api.put("deviceId", getDeviceId());
        api.post("security.checkVerify");
        this.setMessage(api.getMessage());
        return api.isResult();
    }

    public boolean encodeQrcode(String json) {
        JayunAPI api = new JayunAPI(request);
        api.put("ip", api.getRemoteIP());
        api.put("data", json);
        api.post("security.encodeQrcode");
        this.data = api.getData();
        this.setMessage(api.getMessage());
        return api.isResult();
    }

    /**
     * 环境安全检测及校验安全码，一般配合 jsp 使用文件
     *
     * @param user 用户账号
     * @return true:成功，若失败可用getMessage取得错误信息
     */
    public boolean checkEnvironment(String user) {
        String securityValue = request.getParameter(securityCode);
        if (securityValue != null && !"".equals(securityValue)) {
            return checkVerify(user, securityValue);
        } else {
            return isSecurity(user);
        }
    }

    @Deprecated
    public boolean send(String user, String templateId, String... args) {
        JayunMessage message = new JayunMessage(request);
        return message.sendMessage(user, templateId, args);
    }

    private String getDeviceId() {
        String device = (String) request.getSession().getAttribute(deviceId);
        return device == null ? "" : device;
    }

    public SendMode getSendMode() {
        return sendMode;
    }

    public void setSendMode(SendMode sendMode) {
        this.sendMode = sendMode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

}
