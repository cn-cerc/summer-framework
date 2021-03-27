package cn.cerc.ui.parts;

import cn.cerc.ui.core.HtmlWriter;

public abstract class UICssComponent extends UIComponent {
    protected String cssClass;
    @Deprecated
    protected String cssStyle;
    
    public UICssComponent() {
        super();
    }

    public UICssComponent(UIComponent owner) {
        super(owner);
    }

    public String getCssClass() {
        return cssClass;
    }

    public UICssComponent setCssClass(String cssClass) {
        this.cssClass = cssClass;
        return this;
    }

    @Deprecated
    public String getCssStyle() {
        return cssStyle;
    }

    @Deprecated
    public void setCssStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

    @Deprecated
    protected void outputCss(HtmlWriter html) {
        if (this.cssClass != null) {
            html.print(" class='%s'", cssClass);
        }
        if (this.cssStyle != null) {
            html.print(" style='%s'", cssStyle);
        }
    }
}
