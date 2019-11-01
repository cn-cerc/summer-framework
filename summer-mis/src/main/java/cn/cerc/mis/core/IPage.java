package cn.cerc.mis.core;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IPage {
    public IForm getForm();

    public void setForm(IForm form);

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
