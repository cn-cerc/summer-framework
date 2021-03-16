package cn.cerc.ui.vcl;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class UILabel extends UIComponent {
    private String text;
    private String url;
    private String focusTarget;

    public UILabel(UIComponent component) {
        super(component);
    }

    public UILabel() {
        super();
    }
    
    public UILabel(String text) {
        super();
        this.text = text;
    }

    public UILabel(String text, String url) {
        super();
        this.text = text;
        this.url = url;
    }

    public String getFocusTarget() {
        return focusTarget;
    }

    public void setFocusTarget(String focusTarget) {
        this.focusTarget = focusTarget;
    }

    @Override
    public void output(HtmlWriter html) {
        if (url == null) {
            html.print("<label");
            if (focusTarget != null) {
                html.print(" for='%s'", focusTarget);
            }
            super.outputCss(html);
            html.print(">%s</label>", this.text);
        } else {
            html.print("<a href='%s'>%s</a>", this.url, this.text);
        }
    }

    /**
     * 请改使用 getText
     * @return
     */
    @Deprecated
    public String getCaption() {
        return text;
    }

    /**
     * 请改使用 setText
     * @param caption
     */
    @Deprecated 
    public void setCaption(String caption) {
        this.text = caption;
    }

    public String getText() {
        return text;
    }

    public UILabel setText(String text) {
        this.text = text;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public UILabel setUrl(String url) {
        this.url = url;
        return this;
    }

}
