package cn.cerc.ui.vcl.table;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.parts.UICssComponent;

public class UITh extends UICssComponent {
    private String text;

    public UITh() {
        super();
    }

    public UITh(UIComponent component) {
        super(component);
    }

    @Override
    public void output(HtmlWriter html) {
        html.print("<th");
        if (this.getCssClass() != null)
            html.print(" class=\"%s\"", this.getCssClass());
        html.print(">");
        if (this.text != null) {
            html.print(text);
        }
        for (UIComponent item : this)
            item.output(html);
        html.print("</th>");
    }

    public String getText() {
        return text;
    }

    public UITh setText(String text) {
        this.text = text;
        return this;
    }

}
