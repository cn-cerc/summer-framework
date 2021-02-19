package cn.cerc.ui.phone;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UITextBox;
import cn.cerc.ui.vcl.ext.UISpan;

public class Block108 extends UIComponent {
    private UISpan label = new UISpan();
    private UITextBox input = new UITextBox();

    /**
     * 文本 + 输入框
     * 
     * @param owner
     *            内容显示区
     */
    public Block108(UIComponent owner) {
        super(owner);
        label.setText("(label)");
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<div class='block108'>");
        label.output(html);
        input.output(html);
        html.println("</div>");
    }

    public UISpan getLabel() {
        return label;
    }

    public UITextBox getInput() {
        return input;
    }
}
