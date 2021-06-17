package cn.cerc.ui.columns;

import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.UIOriginComponent;
import cn.cerc.ui.parts.UIComponent;

public class UIGridLine extends UIOriginComponent {

    public UIGridLine(UIComponent owner) {
        super(owner);
    }

    @Override
    public void addComponent(Component component) {
        if (!(component instanceof IColumn)) {
            throw new RuntimeException("component is not IColumn");
        }
        super.addComponent(component);
    }
}
