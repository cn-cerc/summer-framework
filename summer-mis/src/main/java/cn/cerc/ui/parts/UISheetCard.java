package cn.cerc.ui.parts;

import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.other.UrlMenu;

public class UISheetCard extends UISheet {
    private UrlMenu url;

    public UISheetCard(UIContent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<section role='sheetCard'>");
        html.print("<label>");
        html.println(this.getCaption());
        if (url != null)
            url.output(html);
        html.println("</label>");
        for (Component component : this.getComponents()) {
            if (component instanceof UIComponent) {
                html.print("<div>");
                ((UIComponent) component).output(html);
                html.print("</div>");
            }
        }
        html.println("</section>");
    }

    public UrlMenu getUrl() {
        if (url == null)
            url = new UrlMenu(null);
        return url;
    }

    public void setUrl(UrlMenu url) {
        this.url = url;
    }
}