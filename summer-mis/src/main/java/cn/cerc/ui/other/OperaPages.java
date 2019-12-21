package cn.cerc.ui.other;

import javax.servlet.http.HttpServletRequest;

import cn.cerc.mis.core.IForm;
import cn.cerc.mis.language.R;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.grid.MutiPage;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.parts.UISheet;

public class OperaPages extends UISheet {
    private IForm form;
    private MutiPage pages;

    public OperaPages(UIComponent owner, IForm form, MutiPage pages) {
        super(owner);
        this.form = form;
        this.pages = pages;
    }

    @Override
    public void output(HtmlWriter html) {
        if (pages.getCount() <= 1)
            return;

        HttpServletRequest request = form.getRequest();
        StringBuffer url = new StringBuffer();
        if (request.getQueryString() != null) {
            String[] items = request.getQueryString().split("&");
            for (String str : items) {
                if (!str.startsWith("pageno=")) {
                    url.append("&");
                    url.append(str);
                }
            }
        }
        boolean isPhone = form.getClient().isPhone();
        if (isPhone) {
            html.println("<div class=\"foot-page\">");
        } else {
            html.println("<section>");
            html.println("<div class=\"title\">%s</div>", R.asString(form.getHandle(), "数据分页"));
            html.println("<div class=\"contents\">");
            html.println("%s：%d, %s：%d，%s：%d <br/>", R.asString(form.getHandle(), "总记录数"), pages.getRecordCount(),
                    R.asString(form.getHandle(), "当前页"), pages.getCurrent(), R.asString(form.getHandle(), "总页数"),
                    pages.getCount());
            html.println("<div align=\"center\">");
        }
        html.println("<a href=\"?pageno=1%s\">%s</a>", url, R.asString(form.getHandle(), "首页"));
        html.println("<a href=\"?pageno=%d%s\">%s</a>", pages.getPrior(), url, R.asString(form.getHandle(), "上一页"));
        html.println("<a href=\"?pageno=%d%s\">%s</a>", pages.getNext(), url, R.asString(form.getHandle(), "下一页"));
        html.println("<a href=\"?pageno=%d%s\">%s</a>", pages.getCount(), url, R.asString(form.getHandle(), "尾页"));
        if (isPhone) {
            html.println("%s：%s, %s：%d / %d", R.asString(form.getHandle(), "笔数"), pages.getRecordCount(),
                    R.asString(form.getHandle(), "页数"), pages.getCurrent(), pages.getCount());
            html.println("</div>");
        } else {
            html.println("</div>");
            html.println("</div>");
            html.println("</section>");
        }
    }
}
