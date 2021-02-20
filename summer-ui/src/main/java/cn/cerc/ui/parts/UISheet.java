package cn.cerc.ui.parts;

public class UISheet extends UIComponent {
    private String caption = "(无标题)";
    private String group = "工具面板";

    @Deprecated
    public UISheet() {
        super(null);
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
