package cn.cerc.mis.core;

import cn.cerc.db.core.IHandle;

public interface IUserLoginCheck extends IHandle {

    // 登录验证
    boolean check(String userCode, String password, String machineCode, String clientIP, String language);

    // 返回 token
    String getToken();

    // 返回手机号
    String getMobile();

    // 通过手机号获取帐号
    String getUserCode(String mobile);

    // 错误消息
    String getMessage();
}
