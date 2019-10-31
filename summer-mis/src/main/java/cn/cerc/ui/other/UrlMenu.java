package cn.cerc.ui.other;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class UrlMenu extends UIComponent {
    private String name;
    private String url;

    public UrlMenu(UIComponent owner) {
        super(owner);
    }

    public UrlMenu(UIComponent owner, String name, String url) {
        super(owner);
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public UrlMenu setName(String name) {
        this.name = name;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public UrlMenu setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public void output(HtmlWriter html) {
        html.print("<a href=\"%s\"", this.url);
        if (this.getId() != null)
            html.print(" id=\"%s\"", this.getId());
        super.outputCss(html);
        html.println(">%s</a>", this.name);
    }

}
