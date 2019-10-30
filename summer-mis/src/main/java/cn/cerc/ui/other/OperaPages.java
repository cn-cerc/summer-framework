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
            html.println("<div class=\"title\">数据分页</div>");
            html.println("<div class=\"contents\">");
            html.println("总记录数：%d, 当前页：%d，总页数：%d <br/>", pages.getRecordCount(), pages.getCurrent(), pages.getCount());
            html.println("<div align=\"center\">");
        }
        html.println("<a href=\"?pageno=1%s\">首页</a>", url);
        html.println("<a href=\"?pageno=%d%s\">上一页</a>", pages.getPrior(), url);
        html.println("<a href=\"?pageno=%d%s\">下一页</a>", pages.getNext(), url);
        html.println("<a href=\"?pageno=%d%s\">尾页</a>", pages.getCount(), url);
        if (isPhone) {
            html.println("笔数：%s, 页数：%d / %d", pages.getRecordCount(), pages.getCurrent(), pages.getCount());
            html.println("</div>");
        } else {
            html.println("</div>");
            html.println("</div>");
            html.println("</section>");
        }
    }
}
