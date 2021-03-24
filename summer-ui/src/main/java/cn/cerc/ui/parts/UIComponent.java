package cn.cerc.ui.parts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.HtmlWriter;

public abstract class UIComponent extends Component implements Iterable<UIComponent> {

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

    @Override
    public Iterator<UIComponent> iterator() {
        List<UIComponent> list = new ArrayList<>();
        for(Component component: this.getComponents()) {
            if(component instanceof UIComponent) {
                list.add((UIComponent) component);
            }
        }
        return list.iterator();
    }

}
