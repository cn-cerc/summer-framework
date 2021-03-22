package cn.cerc.mis.security.sapi;

import javax.servlet.http.HttpServletRequest;

public class JayunMessage {
    /**
     * 发送模式: 默认为手机简讯，可支持语音发送以及其它方式
     */
    private SendMode sendMode = SendMode.SMS;
    private HttpServletRequest request;
    private String message;

    public JayunMessage(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * 新用户注册时，需填写手机号，并请求向此手机号发送简讯
     *
     * @param mobile 请求注册的用户手机号
     * @return 返回是否执行成功
     */
    public boolean requestRegister(String mobile) {
        JayunAPI api = new JayunAPI(request);
        api.put("ip", api.getRemoteIP());
        api.put("mobile", mobile == null ? "" : mobile);
        api.put("sendMode", sendMode.name().toLowerCase());
        if (sendMode == SendMode.VOICE)
            api.put("sendVoice", "true");
        api.post("message.requestRegister");
        this.setMessage(api.getMessage());
        return api.isResult();
    }

    /**
     * 向新用户请求注册的手机号发送语音简讯，请改使用setSendMode(SendMode.voice)方式调用
     *
     * @param mobile    请求注册用户的手机号
     * @param sendVoice true: 发送语音验证码;false:发送普通短信验证码
     * @return 返回是否执行成功
     */
    @Deprecated
    public boolean requestRegister(String mobile, boolean sendVoice) {
        if (sendVoice)
            this.sendMode = SendMode.VOICE;
        return requestRegister(mobile);
    }

    /**
     * 在requestRegister发送验证码后，调用此函数检查其收到的验证码是否正确
     *
     * @param mobile     手机号
     * @param verifyCode 验证码
     * @return 返回是否执行成功
     */
    public boolean checkRegister(String mobile, String verifyCode) {
        JayunAPI api = new JayunAPI(request);
        api.put("ip", api.getRemoteIP());
        api.put("mobile", mobile == null ? "" : mobile);
        api.put("verifyCode", verifyCode == null ? "" : verifyCode);
        api.post("message.checkRegister");
        this.setMessage(api.getMessage());
        return api.isResult();
    }

    @Deprecated // 此函数已移至 JayunMessage.sendMessage
    public boolean send(String user, String templateId, String... args) {
        return sendMessage(user, templateId, args);
    }

    /**
     * 发送指定的模版消息，一般用于通知帐户余额等
     *
     * @param user       用户帐号
     * @param templateId 消息模版 id
     * @param args       调用消息模版参数
     * @return 返回是否执行成功
     */
    public boolean sendMessage(String user, String templateId, String... args) {
        JayunAPI api = new JayunAPI(request);
        api.put("ip", api.getRemoteIP());
        api.put("user", user == null ? "" : user);
        api.put("templateId", templateId == null ? "" : templateId);
        for (int i = 0; i < args.length; i++) {
            api.put("arg" + i, args[i]);
        }
        api.post("message.sendMessage");
        this.setMessage(api.getMessage());
        return api.isResult();
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
}
