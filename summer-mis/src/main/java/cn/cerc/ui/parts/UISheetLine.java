package cn.cerc.ui.parts;

import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.other.UrlMenu;

public class UISheetLine extends UISheet {
    private UrlMenu operaUrl;

    public UISheetLine(UIComponent owner) {
        super(owner);
        this.setCaption("数据合计");
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<section>");
        html.print("<div class=\"title\">");
        html.print(this.getCaption());
        if (operaUrl != null) {
            operaUrl.output(html);
        }
        html.println("</div>");
        html.println("<div class=\"contents\">");
        html.println("<ul>");
        for (Component component : getComponents()) {
            if (component instanceof UIComponent) {
                html.print("<li>");
                ((UIComponent) component).output(html);
                html.print("</li>");
            }
        }
        html.println("</ul>");
        html.println("</div>");
        html.println("</section>");
    }

    public UrlMenu getOperaUrl() {
        if (operaUrl == null) {
            operaUrl = new UrlMenu(null);
            operaUrl.setCssStyle("float:right;line-height:1.25em;margin-bottom:0.25em");
        }
        return operaUrl;
    }

    public void setOperaUrl(UrlMenu operaUrl) {
        this.operaUrl = operaUrl;
    }
}
