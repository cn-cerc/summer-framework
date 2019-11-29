package cn.cerc.mis.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IPage {
    public void setForm(IForm form);

    public IForm getForm();

    public String execute() throws ServletException, IOException;

    default public HttpServletRequest getRequest() {
        IForm form = getForm();
        return form != null ? form.getRequest() : null;
    }

    default public HttpServletResponse getResponse() {
        IForm form = getForm();
        return form != null ? form.getResponse() : null;
    }
}
