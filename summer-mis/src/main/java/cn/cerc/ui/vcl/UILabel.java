package cn.cerc.ui.vcl;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class UILabel extends UIComponent {
    private String caption;
    private String url;
    private String focusTarget;

    public String getFocusTarget() {
        return focusTarget;
    }

    public void setFocusTarget(String focusTarget) {
        this.focusTarget = focusTarget;
    }

    public UILabel(UIComponent component) {
        super(component);
    }

    public UILabel() {
        super();
    }

    @Override
    public void output(HtmlWriter html) {
        if (url == null) {
            html.print("<label");
            if (focusTarget != null)
                html.print(" for='%s'", focusTarget);
            super.outputCss(html);
            html.print(">%s</label>", this.caption);
        } else
            html.print("<a href='%s'>%s</a>", this.url, this.caption);
    }

    public UILabel(String caption, String url) {
        super();
        this.caption = caption;
        this.url = url;
    }

    public UILabel(String caption) {
        super();
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
