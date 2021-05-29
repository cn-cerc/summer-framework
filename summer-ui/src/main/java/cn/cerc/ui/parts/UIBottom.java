package cn.cerc.ui.parts;

import cn.cerc.core.Utils;
import cn.cerc.ui.core.HtmlWriter;

public class UIBottom extends UICssComponent {
    private String caption;
    private String url;
    private String target;

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

    public String getTarget() {
        return target;
    }

    public UIBottom setTarget(String target) {
        this.target = target;
        return this;
    }

    @Override
    public void output(HtmlWriter html) {
        html.print("<a href=\"%s\"", this.url);
        if (this.getId() != null) {
            html.print(" id=\"%s\"", this.getId());
        }
        if (!Utils.isEmpty(this.getTarget())) {
            html.print(" target=\"%s\"", this.target);
        }
        if (this.getCssClass() != null) {
            html.print(" class=\"%s\"", this.getCssClass());
        }
        super.outputCss(html);
        html.println(">%s</a>", this.caption);
    }
}
