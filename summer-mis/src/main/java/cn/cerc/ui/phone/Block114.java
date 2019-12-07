package cn.cerc.ui.phone;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UICheckBox;
import cn.cerc.ui.vcl.ext.UILabelBox;
import cn.cerc.ui.vcl.ext.UISpan;

public class Block114 extends UIComponent {
    private UISpan label = new UISpan();
    private UICheckBox checkBox = new UICheckBox();
    private UILabelBox labelBox = new UILabelBox();

    /**
     * 文本 + 选择框 + 说明
     * <p>
     * 例如用于显示下游品牌授权
     * 
     * @param owner
     *            内容显示区
     */
    public Block114(UIComponent owner) {
        super(owner);
        label.setText("(label)");
        labelBox.setText("(label)");
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<div class='block114'>");
        label.output(html);
        checkBox.output(html);
        labelBox.output(html);
        html.println("</div>");
    }

    public UISpan getLabel() {
        return label;
    }

    public UICheckBox getCheckBox() {
        return checkBox;
    }

    public UILabelBox getLabelBox() {
        return labelBox;
    }

}
