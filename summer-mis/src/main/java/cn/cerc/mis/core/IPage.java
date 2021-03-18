package cn.cerc.mis.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IPage extends IView {

    default HttpServletRequest getRequest() {
        IForm form = getForm();
        return form != null ? form.getRequest() : null;
    }

    default HttpServletResponse getResponse() {
        IForm form = getForm();
        return form != null ? form.getResponse() : null;
    }
}
