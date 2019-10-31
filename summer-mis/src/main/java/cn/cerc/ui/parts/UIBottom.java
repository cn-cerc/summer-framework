package cn.cerc.ui.parts;

import cn.cerc.ui.core.HtmlWriter;

public class UIBottom extends UIComponent {
    private String caption;
    private String url;

    public UIBottom(UIComponent owner) {
        super(owner);
    }

    public String getName() {
        return caption;
    }

    public UIBottom setCaption(String caption) {
        this.caption = caption;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public UIBottom setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public void output(HtmlWriter html) {
        html.print("<a href=\"%s\"", this.url);
        if (this.getId() != null)
            html.print(" id=\"%s\"", this.getId());
        super.outputCss(html);
        html.println(">%s</a>", this.caption);
    }
}
