package cn.cerc.ui.vcl;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.ui.core.UICustomComponent;
import cn.cerc.ui.core.HtmlContent;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class UIDiv extends UICustomComponent {
    private List<HtmlContent> contents = new ArrayList<>();
    private String text = null;

    public UIDiv() {
        super();
    }

    public UIDiv(UICustomComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        html.print("<div");
        super.outputCss(html);
        html.println(">");

        super.output(html);

        if (text != null)
            html.print(text);

        // 输出追加过来的内容
        for (HtmlContent content : contents) {
            content.output(html);
        }
        html.println("</div>");
    }

    public UIDiv setText(String text) {
        this.text = text;
        return this;
    }

    public String getText() {
        return text;
    }

}
