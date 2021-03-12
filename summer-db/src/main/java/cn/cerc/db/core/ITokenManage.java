package cn.cerc.db.core;

public interface ITokenManage {
    // 根据帐号密码等，生成新的 token，原命名为 init
    boolean createToken(String bookNo, String userCode, String password, String machineCode);

    // 根据 token 恢复作业环境，原命名为 init
    boolean resumeToken(String token);
}
