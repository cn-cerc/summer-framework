package cn.cerc.mis.core;

import javax.servlet.http.HttpServletRequest;

public interface IRequestOwner {

    HttpServletRequest getRequest();

    void setRequest(HttpServletRequest request);

}
