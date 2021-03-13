package cn.cerc.mis.core;

import javax.servlet.ServletException;
import java.io.IOException;

public interface IAppLogin {

    void init(IForm form);

    // 1、检查是否已经登记，若未登记，则返回登录页的jsp文件名，否则返回null即可
    String checkToken(String token) throws IOException, ServletException;

    // 2、在用户于登录页上输入用户名及密码时，应影响此函数
    String checkLogin(String userCode, String password) throws IOException, ServletException;

}
