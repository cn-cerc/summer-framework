package cn.cerc.mis.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.cerc.core.IHandle;

public interface IForm {
    // 页面标题
    public String getTitle();

    public void setRequest(HttpServletRequest request);

    public HttpServletRequest getRequest();

    public void setResponse(HttpServletResponse response);

    public HttpServletResponse getResponse();

    // 数据库连接
    public void setHandle(IHandle handle);

    public IHandle getHandle();

    // 是否有登录
    public boolean logon();

    // 取得访问设备讯息
    public IClient getClient();

    public void setClient(IClient client);

    // 设置参数
    public void setParam(String key, String value);

    // 取得参数
    public String getParam(String key, String def);

    // 输出页面（支持jsp、reddirect、json等）
    public IPage execute() throws Exception;

    // 取得权限代码
    public String getPermission();

    // 设备安全检查通过否，为true时需要进行进一步授权
    default public boolean passDevice() {
        return false;
    }

}
