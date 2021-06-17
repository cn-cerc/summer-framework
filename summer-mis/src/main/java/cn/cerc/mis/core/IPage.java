package cn.cerc.mis.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IPage {
    
    IForm getForm();

    void setForm(IForm form);

    String execute() throws ServletException, IOException;

    default HttpServletRequest getRequest() {
        IForm form = getForm();
        return form != null ? form.getRequest() : null;
    }

    default HttpServletResponse getResponse() {
        IForm form = getForm();
        return form != null ? form.getResponse() : null;
    }
}
