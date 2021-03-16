package cn.cerc.ui.vcl;

import cn.cerc.ui.parts.UIComponent;

public class UIButtonSubmit extends UIButton {

    public UIButtonSubmit(UIComponent owner) {
        super(owner);
        this.setType("submit");
        this.setName("submit");
        this.setText("submit");
        this.setValue("submit");
    }

}
