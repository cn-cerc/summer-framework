package cn.cerc.mis.core;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import cn.cerc.db.core.IHandle;
import cn.cerc.mis.config.ApplicationConfig;

import java.io.IOException;

public interface IAppLogin extends IHandle{

    void init(IForm form);

    // 1、检查是否已经登记，若未登记，则返回登录页的jsp文件名，否则返回null即可
    String checkToken(String token) throws IOException, ServletException;

    // 2、在用户于登录页上输入用户名及密码时，应影响此函数
    String checkLogin(String userCode, String password) throws IOException, ServletException;

    default boolean pass(IForm form) throws IOException, ServletException {
        init(form);
        String loginJspFile = null;
        AppClient client = (AppClient) form.getClient();
        HttpServletRequest req = form.getRequest();
        // 若页面有传递用户帐号，则强制重新校验
        if (form.getRequest().getParameter("login_usr") != null) {
            // 检查服务器的角色状态，如果非从服务器，则允许登录
            if (ApplicationConfig.isReplica()) {
                //FIXME 此处翻译待处理
                throw new RuntimeException("当前服务不支持登录，请返回首页重新登录");
            }
            String login_usr = req.getParameter("login_usr");
            String login_pwd = req.getParameter("login_pwd");
            loginJspFile = checkLogin(login_usr, login_pwd);
        } else {
            // 检查session或url中是否存在sid
            loginJspFile = checkToken(client.getToken());
        }
        setJspFile(loginJspFile);
        return loginJspFile == null;
    }

    void setJspFile(String loginJspFile);
    
   String getJspFile();

}
