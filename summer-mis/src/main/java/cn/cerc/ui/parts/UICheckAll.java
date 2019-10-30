package cn.cerc.ui.parts;

import cn.cerc.ui.core.HtmlWriter;

public class UICheckAll extends UIComponent {
    private String targetId;
    private String caption = "全选";
    private String onclick = "selectItems";

    public UICheckAll(UIFooterOperation owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        html.print("<input type='checkbox' id='selectAll'");
        html.print(" onclick=\"%s\"/>", this.getOnclick());
        html.println("<label for=\"selectAll\">%s</label>", this.getCaption());
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
        this.setOnclick(String.format("selectItems('%s')", targetId));
    }

    public String getOnclick() {
        return onclick;
    }

    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

}
