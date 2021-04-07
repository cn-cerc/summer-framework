package cn.cerc.mis.core;

import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.IHandleOwner;

public interface IForm extends IHandle, IHandleOwner, IRequestOwner, IResponseOwner, IPermission {

    // 页面代码
    void setId(String formId);

    String getId();
    
    // 页面名称
    String getName();

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

    @Deprecated
    default Object getProperty(String key) {
        return getSession().getProperty(key);
    }

    @Deprecated
    default void setProperty(String key, Object value) {
        getSession().setProperty(key, value);
    }
    
}
