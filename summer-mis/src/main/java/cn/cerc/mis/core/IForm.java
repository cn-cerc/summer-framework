package cn.cerc.mis.core;

import cn.cerc.core.IHandle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IForm {
    // 页面标题
    String getTitle();

    HttpServletRequest getRequest();

    void setRequest(HttpServletRequest request);

    HttpServletResponse getResponse();

    void setResponse(HttpServletResponse response);

    IHandle getHandle();

    // 数据库连接
    void setHandle(IHandle handle);

    // 是否有登录
    boolean logon();

    // 取得访问设备讯息
    IClient getClient();

    void setClient(IClient client);

    // 设置参数
    void setParam(String key, String value);

    // 取得参数
    String getParam(String key, String def);

    // 输出页面（支持jsp、reddirect、json等）
    IPage execute() throws Exception;

    // 取得权限代码
    String getPermission();

    // 设备安全检查通过否，为true时需要进行进一步授权
    default boolean passDevice() {
        return false;
    }

}
