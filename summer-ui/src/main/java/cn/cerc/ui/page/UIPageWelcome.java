package cn.cerc.ui.page;

import java.io.PrintWriter;

import cn.cerc.mis.core.IForm;

public class UIPageWelcome extends UIPage {

    public UIPageWelcome(IForm form) {
        super();
        this.setForm(form);
    }

    @Override
    protected void writeHtml(PrintWriter out) {
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        writeHead(out);
        out.println("</head>");
        out.println("<body>");
        writeBody(out);
        out.println("</body>");
        out.println("</html>");
    }


}
