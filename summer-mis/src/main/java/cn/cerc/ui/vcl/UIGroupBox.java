package cn.cerc.ui.vcl;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class UIGroupBox extends UIComponent {

    private String cssClass;

    public UIGroupBox(UIComponent content) {
        super(content);
    }

    @Override
    public void output(HtmlWriter html) {
        html.print("<div role='group'");
        if (getId() != null)
            html.print(" id='%s' ", getId());
        if (this.cssClass != null)
            html.print(" class='%s' ", cssClass);
        html.print(">");
        super.output(html);
        html.println("</div>");
    }

    @Override
    public String getCssClass() {
        return cssClass;
    }

    @Override
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }
}
