package cn.cerc.mis.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Deprecated
//TODO 此处抽象有误，不容易使用，需要改进
public interface IAppErrorPage {
    String getErrorPage(HttpServletRequest req, HttpServletResponse resp, Throwable error);
}
