package cn.cerc.core;

public interface IConnection {
    String getClientId();

    // 返回会话
    public Object getClient();

    void setConfig(IConfig config);
}
