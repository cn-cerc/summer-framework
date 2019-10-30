package cn.cerc.ui.phone;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UIButton;
import cn.cerc.ui.vcl.UITextArea;
import cn.cerc.ui.vcl.ext.UISpan;

public class Block111 extends UIComponent {
    private UISpan label = new UISpan();
    private UITextArea input = new UITextArea();
    private UIButton search = new UIButton();

    /**
     * 文本 + 输入框 + 查询按钮
     * 
     * @param owner
     *            内容显示区
     */
    public Block111(UIComponent owner) {
        super(owner);
        label.setText("(label)");
        search.setText("查询");
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<div class='block111'>");
        label.output(html);
        input.output(html);
        search.output(html);
        html.println("</div>");
    }

    public UISpan getLabel() {
        return label;
    }

    public UITextArea getInput() {
        return input;
    }

    public UIButton getSearch() {
        return search;
    }
}
