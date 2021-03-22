package cn.cerc.ui.parts;

import cn.cerc.ui.core.UICustomComponent;

public class UIFooterOperation extends UICustomComponent {
    private UICheckAll checkAll;

    public UIFooterOperation(UICustomComponent owner) {
        super(owner);
    }

    public UICheckAll getCheckAll() {
        if (checkAll == null) {
            checkAll = new UICheckAll(this);
        }
        return checkAll;
    }
}
