package cn.cerc.ui.vcl;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UICssComponent;

public class UIUrl extends UICssComponent {
    private String text;
    private String href;
    private String onclick;

    @Override
    public void output(HtmlWriter html) {
        html.print("<a");
        if (this.getCssClass() != null)
            html.print(" class=\"%s\"", this.getCssClass());
        if (this.href != null)
            html.print(" href=\"%s\"", this.href);
        if (this.href != null)
            html.print(" onclick=\"%s\"", this.onclick);
        html.print(">");

        if (this.text != null)
            html.print(this.text);

        html.print("</a>");
    }

    public String getText() {
        return text;
    }

    public UIUrl setText(String text) {
        this.text = text;
        return this;
    }

    public String getHref() {
        return href;
    }

    public UIUrl setHref(String href) {
        this.href = href;
        return this;
    }

    public UIUrl setHref(String href, Object... args) {
        this.href = String.format(href, args);
        return this;
    }

    public String getOnclick() {
        return onclick;
    }

    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

}
