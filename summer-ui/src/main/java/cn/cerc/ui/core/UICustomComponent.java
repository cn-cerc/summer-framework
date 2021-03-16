package cn.cerc.ui.core;

import cn.cerc.ui.parts.UIComponent;

public class UICustomComponent extends UIComponent {
    
    public UICustomComponent() {
        super();
    }

    public UICustomComponent(UICustomComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        for (Component component : this.getComponents()) {
            if (component instanceof UIComponent) {
                ((UIComponent) component).output(html);
            }
        }
    }

}
