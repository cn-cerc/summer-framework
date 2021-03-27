package cn.cerc.ui.vcl.table;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.parts.UICssComponent;

public class UITr extends UICssComponent {

    public UITr() {
        super();
    }

    public UITr(UIComponent component) {
        super(component);
    }

    @Override
    public void output(HtmlWriter html) {
        html.print("<tr");
        if (this.getCssClass() != null)
            html.print(" class=\"%s\"", this.getCssClass());
        html.print(">");
        for (UIComponent item : this)
            item.output(html);
        html.print("</tr>");
    }
    
    @Override
    public UITr setCssClass(String cssClass) {
        super.setCssClass(cssClass);
        return this;
    }
}
