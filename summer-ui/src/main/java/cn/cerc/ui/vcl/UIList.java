package cn.cerc.ui.vcl;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class UIList extends UIComponent {
    private String cssClass;

    public UIList() {
        super();
    }

    public UIList(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        html.print("<ul");
        if (this.cssClass != null)
            html.print(" class=\"%s\"", this.cssClass);
        html.print(">");
        for (UIComponent item : this) {
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

}
