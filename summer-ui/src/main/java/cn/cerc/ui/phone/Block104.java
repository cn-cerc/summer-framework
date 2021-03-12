package cn.cerc.ui.phone;

import cn.cerc.core.ClassResource;
import cn.cerc.ui.SummerUI;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UIButton;
import cn.cerc.ui.vcl.UITextBox;

public class Block104 extends UIComponent {
    private static final ClassResource res = new ClassResource(Block104.class, SummerUI.ID);

    private UITextBox input;
    private UIButton submit;

    /**
     * 通用搜索条件框
     *
     * @param owner 内容显示区域
     */
    public Block104(UIComponent owner) {
        super(owner);
        input = new UITextBox(this);
        input.setPlaceholder(res.getString(1, "请输入搜索条件"));
        submit = new UIButton(this);
        submit.setText(res.getString(2, "搜索"));
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<div class='block104'>");
        html.print("<span>");
        input.output(html);
        submit.output(html);
        html.print("</span>");
        html.println("</div>");
    }

    public UITextBox getInput() {
        return input;
    }

    public UIButton getSubmit() {
        return submit;
    }
}
