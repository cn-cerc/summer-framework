package cn.cerc.ui.vcl;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class UIList extends UIComponent {
    private String cssClass;
    private List<UIComponent> items = new ArrayList<>();

    @Override
    public void output(HtmlWriter html) {
        html.print("<ul");
        if (this.cssClass != null)
            html.print(" class='%s'>", this.cssClass);
        for (UIComponent item : items) {
            html.print("<li>");
            item.output(html);
            html.print("</li>");
        }
        html.println("</ul>");
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public List<UIComponent> getItems() {
        return items;
    }

    public void add(UIComponent component) {
        items.add(component);
    }
}
