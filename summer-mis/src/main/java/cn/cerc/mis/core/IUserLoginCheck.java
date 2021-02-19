package cn.cerc.mis.core;

public interface IUserLoginCheck {

    // 登录验证
    public boolean check(String userCode, String password, String deviceId, String clientIP, String languageId);

    // 返回SessionId
    public String getSessionId();

    // 返回手机号
    public String getMobile();

    // 通过手机号获取帐号
    public String getTelToUserCode(String mobile);

    // 错误消息
    public String getMessage();
}
