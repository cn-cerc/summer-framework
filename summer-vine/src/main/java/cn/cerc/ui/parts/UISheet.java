package cn.cerc.ui.parts;

import cn.cerc.core.ClassResource;
import cn.cerc.ui.SummerUI;
import cn.cerc.ui.core.UICustomComponent;

public class UISheet extends UICustomComponent {
    private static final ClassResource res = new ClassResource(UISheet.class, SummerUI.ID);

    private String caption = res.getString(1, "(无标题)");
    private String group = res.getString(2, "工具面板");

    public UISheet() {
        super();
    }

    public UISheet(UIComponent owner) {
        super(owner);
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
