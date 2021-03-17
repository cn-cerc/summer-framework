package cn.cerc.ui.parts;

import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.HtmlWriter;

public abstract class UIComponent extends Component {

    public UIComponent() {
        super();
    }

    public UIComponent(UIComponent owner) {
        super(owner);
    }

    public abstract void output(HtmlWriter html);

    @Override
    public final String toString() {
        HtmlWriter html = new HtmlWriter();
        output(html);
        return html.toString();
    }

}
