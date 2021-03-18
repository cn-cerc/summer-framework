package cn.cerc.mis.core;

import cn.cerc.db.core.IHandle;

public interface IForm extends IHandle, IRequestOwner, IResponseOwner, IPermission {

    // 页面代码
    void setId(String formId);

    String getId();

    // 页面名称
    String getName();

    IHandle getHandle();

    // 数据库连接
    void setHandle(IHandle handle);

    // 取得访问设备讯息
    IClient getClient();

    void setClient(IClient client);

    // 设置参数
    void setParam(String key, String value);

    // 取得参数
    String getParam(String key, String def);

    // 输出页面（支持jsp、reddirect、json等）
    IPage execute() throws Exception;

    // 执行指定函数，并返回jsp文件名，若自行处理输出则直接返回null
    String getView(String funcId) throws Exception;

    void setPathVariables(String[] pathVariables);

}
