package cn.cerc.mis.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IAppErrorPage {
    String getErrorPage(HttpServletRequest req, HttpServletResponse resp, Throwable error);
}
