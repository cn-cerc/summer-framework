package cn.cerc.ui.parts;

import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.HtmlWriter;

public class UIComponent extends Component {
    protected String cssClass;
    protected String cssStyle;

    public UIComponent() {
        super();
    }

    public UIComponent(UIComponent owner) {
        super(owner);
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getCssStyle() {
        return cssStyle;
    }

    public void setCssStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

    public void output(HtmlWriter html) {
        for (Component component : this.getComponents()) {
            if (component instanceof UIComponent)
                ((UIComponent) component).output(html);
        }
    }

    @Override
    public final String toString() {
        HtmlWriter html = new HtmlWriter();
        output(html);
        return html.toString();
    }

}
