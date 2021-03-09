package cn.cerc.ui.vcl;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.ui.core.HtmlContent;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class UIDiv extends UIComponent {
    private List<HtmlContent> contents = new ArrayList<>();
    
    public UIDiv() {
        super();
    }

    public UIDiv(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        html.print("<div");
        super.outputCss(html);
        html.println(">");

        super.output(html);
        // 输出追加过来的内容
        for (HtmlContent content : contents) {
            content.output(html);
        }
        html.println("</div>");
    }
}
