package cn.cerc.mis.core;

import java.io.IOException;

import javax.servlet.ServletException;

public interface IAppLogin {

    // 1、检查是否已经登记，若未登记，则返回登录页的jsp文件名，否则返回null即可
    String getLoginView(IForm form) throws IOException, ServletException;

}
