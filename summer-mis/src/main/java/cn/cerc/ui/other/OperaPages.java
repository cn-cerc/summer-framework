package cn.cerc.ui.other;

import javax.servlet.http.HttpServletRequest;

import cn.cerc.mis.core.IForm;
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
            html.println("<div class=\"title\">數據分頁</div>");
            html.println("<div class=\"contents\">");
            html.println("總記錄數：%d, 當前頁：%d，總頁數：%d <br/>", pages.getRecordCount(), pages.getCurrent(), pages.getCount());
            html.println("<div align=\"center\">");
        }
        html.println("<a href=\"?pageno=1%s\">首頁</a>", url);
        html.println("<a href=\"?pageno=%d%s\">上壹頁</a>", pages.getPrior(), url);
        html.println("<a href=\"?pageno=%d%s\">下壹頁</a>", pages.getNext(), url);
        html.println("<a href=\"?pageno=%d%s\">尾頁</a>", pages.getCount(), url);
        if (isPhone) {
            html.println("筆數：%s, 頁數：%d / %d", pages.getRecordCount(), pages.getCurrent(), pages.getCount());
            html.println("</div>");
        } else {
            html.println("</div>");
            html.println("</div>");
            html.println("</section>");
        }
    }
}
