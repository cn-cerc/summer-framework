package cn.cerc.mis.page;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;

import cn.cerc.mis.core.IForm;
import cn.cerc.mis.core.IPage;
import cn.cerc.ui.core.HtmlWriter;

public class HtmlPage implements IPage {
    private IForm form;
    private HtmlWriter content = new HtmlWriter();

    public HtmlPage(IForm form) {
        super();
        this.setForm(form);
    }

    @Override
    public void setForm(IForm form) {
        this.form = form;
    }

    @Override
    public IForm getForm() {
        return form;
    }

    @Override
    public String execute() throws ServletException, IOException {
        PrintWriter out = form.getResponse().getWriter();
        out.print(content.toString());
        return null;
    }

    public HtmlWriter getContent() {
        return content;
    }

}
