package cn.cerc.ui.vcl;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.UrlRecord;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.parts.UICssComponent;

public class UIUrl extends UICssComponent {
    private String text;
    private String href;
    private String onclick;
    private UrlRecord url;

    public UIUrl() {
        super();
    }

    public UIUrl(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        html.print("<a");
        if (this.getCssClass() != null)
            html.print(" class=\"%s\"", this.getCssClass());
        if (this.getHref() != null)
            html.print(" href=\"%s\"", this.getHref());
        if (this.onclick != null)
            html.print(" onclick=\"%s\"", this.onclick);
        html.print(">");

        if (this.text != null)
            html.print(this.text);

        for (UIComponent item : this) {
            item.output(html);
        }

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
        return url != null ? url.getUrl() : href;
    }

    public UIUrl setHref(String href) {
        this.href = href;
        return this;
    }

    // 此函数特别允许与setHref重复，以方便记忆
    public UIUrl setUrl(String href) {
        this.href = href;
        return this;
    }

    // 此函数特别允许与setHref重复，以方便记忆
    public UIUrl setUrl(String href, Object... args) {
        this.href = String.format(href, args);
        return this;
    }

    public String getOnclick() {
        return onclick;
    }

    public UIUrl setOnclick(String onclick) {
        this.onclick = onclick;
        return this;
    }

    public UIUrl setSite(String site) {
        if (url == null)
            url = new UrlRecord();
        url.setSite(site);
        return this;
    }

    public UIUrl putParam(String key, String value) {
        if (url == null)
            url = new UrlRecord();
        url.putParam(key, value);
        return this;
    }

}
