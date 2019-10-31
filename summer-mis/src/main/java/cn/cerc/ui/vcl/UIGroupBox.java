package cn.cerc.ui.vcl;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class UIGroupBox extends UIComponent {

    public UIGroupBox(UIComponent content) {
        super(content);
    }

    @Override
    public void output(HtmlWriter html) {
        html.print("<div role='group'");
        if (getId() != null)
            html.print(" id='%s'", getId());

        super.outputCss(html);
        html.println(">");

        super.output(html);

        html.println("</div>");
    }

}
