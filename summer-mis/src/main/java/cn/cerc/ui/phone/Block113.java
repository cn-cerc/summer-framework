package cn.cerc.ui.phone;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UITextArea;
import cn.cerc.ui.vcl.ext.UISpan;

public class Block113 extends UIComponent {
    private UISpan label = new UISpan();
    private UITextArea input = new UITextArea();

    /**
     * 文本 + 长文本消息
     * <p>
     * 显示长文本信息，例如收货地址
     * 
     * @param owner
     *            内容显示区
     */
    public Block113(UIComponent owner) {
        super(owner);
        label.setText("(label)");
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<div class='block113'>");
        label.output(html);
        input.output(html);
        html.println("</div>");
    }

    public UISpan getLabel() {
        return label;
    }

    public UITextArea getInput() {
        return input;
    }

}
