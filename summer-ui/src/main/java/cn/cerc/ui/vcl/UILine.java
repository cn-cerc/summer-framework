package cn.cerc.ui.vcl;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class UILine extends UIComponent {

    public UILine(UIComponent form) {
        super(form);
    }

    @Override
    public void output(HtmlWriter html) {
        html.print("<hr/>");
    }

}
