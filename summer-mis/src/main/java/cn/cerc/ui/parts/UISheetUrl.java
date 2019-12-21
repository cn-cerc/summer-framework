package cn.cerc.ui.parts;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.UrlRecord;

public class UISheetUrl extends UISheet {
    private List<UrlRecord> urls = new ArrayList<>();
    // 使用于page-link.xml中
    private Map<String, String> items = new LinkedHashMap<>();
    private boolean isCloseSheet;

    @Deprecated
    public UISheetUrl() {
        super();
        this.setCaption("相关操作");
    }

    public UISheetUrl(UIToolBar owner) {
        super(owner);
        this.setCaption("相关操作");
    }

    @Override
    public void output(HtmlWriter html) {
        if (urls.size() == 0 && items.size() == 0)
            return;

        if (this.isCloseSheet)
            html.println("<section style='display: none;'>");
        else
            html.println("<section>");
        html.println("<div class=\"title\">%s</div>", this.getCaption());
        html.println("<div class=\"contents\">");
        for (UrlRecord url : urls) {
            html.print("<a href=\"%s\"", url.getUrl());
            if (url.getId() != null) {
                html.print(" id=\"%s\"", url.getId());
            }
            if (url.getTitle() != null) {
                html.print(" title=\"%s\"", url.getTitle());
            }
            if (url.getHintMsg() != null) {
                html.print(" onClick=\"return confirm('%s');\"", url.getHintMsg());
            }
            if (url.getTarget() != null) {
                html.print(" target=\"%s\"", url.getTarget());
            }
            html.println(">%s</a>", url.getName());
        }
        for (String key : items.keySet())
            html.println("<a href=\"%s\">%s</a>", key, items.get(key));
        html.println("</div>");
        html.println("</section>");
    }

    public UrlRecord addUrl() {
        UrlRecord url = new UrlRecord();
        urls.add(url);
        return url;
    }

    public UrlRecord addUrl(UrlRecord url) {
        urls.add(url);
        return url;
    }

    public Map<String, String> getItems() {
        return items;
    }

    public void setItems(Map<String, String> items) {
        this.items = items;
    }

    public boolean isCloseSheet() {
        return isCloseSheet;
    }

    public void setCloseSheet(boolean isCloseSheet) {
        this.isCloseSheet = isCloseSheet;
    }
}
