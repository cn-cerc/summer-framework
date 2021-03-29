package cn.cerc.ui.other;

import cn.cerc.core.ClassResource;
import cn.cerc.core.IUserLanguage;
import cn.cerc.mis.core.IForm;
import cn.cerc.mis.language.R;
import cn.cerc.ui.SummerUI;
import cn.cerc.ui.core.UICustomComponent;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.grid.MutiPage;
import cn.cerc.ui.parts.UISheet;

import javax.servlet.http.HttpServletRequest;

public class OperaPages extends UISheet implements IUserLanguage {
    private final ClassResource res = new ClassResource(this, SummerUI.ID);

    private IForm form;
    private MutiPage pages;

    public OperaPages(UICustomComponent owner, IForm form, MutiPage pages) {
        super(owner);
        this.form = form;
        this.pages = pages;
    }

    @Override
    public void output(HtmlWriter html) {
        if (pages.getCount() <= 1) {
            return;
        }

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
            html.println("<div class=\"title\">%s</div>", res.getString(1, "数据分页"));
            html.println("<div class=\"contents\">");
            html.println("%s：%d，%s：%d/%d %s<br/>",
                    res.getString(2, "总记录数"), pages.getRecordCount(),
                    res.getString(3, "当前页"), pages.getCurrent(), pages.getCount(), res.getString(4, "页"));
            html.println("<div align=\"center\">");
        }
        html.println("<a href=\"?pageno=1%s\">%s</a>", url, res.getString(5, "首页"));
        html.println("<a href=\"?pageno=%d%s\">%s</a>", pages.getPrior(), url, res.getString(6, "上一页"));
        html.println("<a href=\"?pageno=%d%s\">%s</a>", pages.getNext(), url, res.getString(7, "下一页"));
        html.println("<a href=\"?pageno=%d%s\">%s</a>", pages.getCount(), url, res.getString(8, "尾页"));
        if (isPhone) {
            html.println("%s：%s, %s：%d / %d", res.getString(9, "笔数"), pages.getRecordCount(),
                    res.getString(10, "页数"), pages.getCurrent(), pages.getCount());
            html.println("</div>");
        } else {
            html.println("</div>");
            html.println("</div>");
            html.println("</section>");
        }
    }

    @Override
    public String getLanguageId() {
        return R.getLanguageId(form.getHandle());
    }

}
