package cn.cerc.ui.parts;

import cn.cerc.ui.core.HtmlWriter;

public class UIMessage extends UIComponent {
    private String text = "";

    public UIMessage(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        html.print("<section role='message'>");
        if (!"".equals(text))
            html.print(text);
        else
            super.output(html);
        html.print("</section>");
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
