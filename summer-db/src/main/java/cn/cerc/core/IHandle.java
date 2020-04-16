package cn.cerc.core;

public interface IHandle {

    // 帐套代码（公司别）
    String getCorpNo();

    // 用户帐号
    String getUserCode();

    // 自定义参数，注：若key=null则返回实现接口的对象本身
    Object getProperty(String key);

    // 用户姓名
    String getUserName();

    // 设置自定义参数
    void setProperty(String key, Object value);

    // 直接设置成登录成功状态，用于定时服务时初始化等，会生成内存临时的token
    boolean init(String bookNo, String userCode, String password, String machineCode);

    // 在登录成功并生成token后，传递token值进行初始化
    boolean init(String token);

    // 返回当前是否为已登入状态
    boolean logon();

    void close();
}
