package cn.cerc.ui.core;

import cn.cerc.ui.parts.UIComponent;

public class UIOriginComponent extends UICustomComponent implements IOriginOwner {
    private Object origin;

    public UIOriginComponent(UIComponent owner) {
        super(owner);
        if (owner instanceof IOriginOwner)
            this.origin = ((IOriginOwner) owner).getOrigin();
    }

    @Override
    public void setOrigin(Object parent) {
        this.origin = parent;
    }

    @Override
    public Object getOrigin() {
        return origin;
    }

}
