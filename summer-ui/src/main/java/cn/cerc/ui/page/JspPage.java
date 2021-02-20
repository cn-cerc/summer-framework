package cn.cerc.ui.page;

import cn.cerc.ui.mvc.AbstractJspPage;
import cn.cerc.mis.core.IForm;

public class JspPage extends AbstractJspPage {

    public JspPage(IForm form) {
        super();
        setForm(form);
    }

    public JspPage(IForm form, String jspFile) {
        super();
        setForm(form);
        this.setJspFile(jspFile);
    }

    public void add(String id, Object value) {
        put(id, value);
    }
}
