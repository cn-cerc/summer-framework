package cn.cerc.ui.phone;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.UrlRecord;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.ext.UISpan;

/**
 * 提示块
 * 
 * @author 郭向军
 *
 */
public class Block125 extends UIComponent {
    private List<UISpan> items = new ArrayList<>();
    private List<UrlRecord> urlRecords = new ArrayList<>();

    public Block125(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<div class='block125'>");
        html.print("<ul>");
        for (int i = 0; i < items.size(); i++) {
            html.print("<li>");
            html.print("<a href='%s'>",
                    this.urlRecords.get(i) == null ? "javascript:void(0);" : this.urlRecords.get(i).getUrl());
            items.get(i).output(html);
            html.print("</a>");
            html.print("</li>");
        }
        html.print("</ul>");
        html.print("</div>");
    }

    public void addItems(String text, UrlRecord url) {
        UISpan span = new UISpan();
        span.setText(text);
        this.items.add(span);
        this.urlRecords.add(url);
    }

    public List<UrlRecord> getUrlRecords() {
        return urlRecords;
    }

}
