package cn.cerc.ui.parts;

public class UIFooterOperation extends UIComponent {
    private UICheckAll checkAll;

    public UIFooterOperation(UIComponent owner) {
        super(owner);
    }

    public UICheckAll getCheckAll() {
        if (checkAll == null)
            checkAll = new UICheckAll(this);
        return checkAll;
    }
}
