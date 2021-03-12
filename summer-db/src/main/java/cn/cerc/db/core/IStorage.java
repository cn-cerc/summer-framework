package cn.cerc.db.core;

import cn.cerc.core.ISession;

public interface IStorage extends ISession{
    // 直接设置成登录成功状态，用于定时服务时初始化等，会生成内存临时的token
    boolean init(String bookNo, String userCode, String password, String machineCode);

    // 在登录成功并生成token后，传递token值进行初始化
    boolean init(String token);

    ISession getSession();
}
