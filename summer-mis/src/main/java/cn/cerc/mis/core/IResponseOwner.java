package cn.cerc.mis.core;

import javax.servlet.http.HttpServletResponse;

public interface IResponseOwner {

    HttpServletResponse getResponse();

    void setResponse(HttpServletResponse response);

}
