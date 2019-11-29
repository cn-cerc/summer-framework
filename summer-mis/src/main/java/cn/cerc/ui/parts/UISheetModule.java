package cn.cerc.ui.parts;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.UrlRecord;

public class UISheetModule extends UISheet {
    private List<UrlRecord> urls = new ArrayList<>();
    // 使用于page-link.xml中
    private Map<String, String> items = new LinkedHashMap<>();

    @Deprecated
    public UISheetModule() {
        super();
    }

    public UISheetModule(UIToolBar owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        if (urls.size() == 0 && items.size() == 0)
            return;

        html.println("<section>");
        html.println("<div class=\"menus_list\">");
        html.println("<ul>");
        for (UrlRecord url : urls) {
            html.println("<li>");
            html.print("<img src='%s'/>", url.getImgage());
            html.print("<a href=\"%s\" onclick=\"updateUserHit('%s')\"", url.getUrl(), url.getUrl());
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
            html.print(">%s</a>", url.getName());
            if (url.getArrow() != null && !"".equals(url.getArrow()))
                html.println("<img src='%s' />", url.getArrow());
            html.println("</li>");
        }
        for (String key : items.keySet())
            html.println("<a href=\"%s\">%s</a>", key, items.get(key));
        html.println("</ul>");
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
}
