package cn.cerc.ui.phone;

import cn.cerc.core.ClassResource;
import cn.cerc.ui.SummerUI;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UIButton;
import cn.cerc.ui.vcl.UIForm;
import cn.cerc.ui.vcl.UITextBox;
import cn.cerc.ui.vcl.ext.UISpan;

/**
 * 一组左边图标右边文字
 *
 * @author 郭向军
 */
public class Block123 extends UIComponent {
    private static final ClassResource res = new ClassResource(Block123.class, SummerUI.ID);

    private UISpan title = new UISpan();
    private UITextBox textBox = new UITextBox();
    private UIButton button = new UIButton();
    private UIForm form = new UIForm();

    public Block123(UIComponent owner) {
        super(owner);
        this.textBox.setMaxlength("20");
        this.textBox.setPlaceholder(res.getString(1, "请输入"));
        this.textBox.setType("text");
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<div class='block123'>");
        this.form.outHead(html);
        this.textBox.output(html);
        this.button.output(html);
        this.form.outFoot(html);
        html.print("</div>");
    }

    public UISpan getTitle() {
        return title;
    }

    public UITextBox getTextBox() {
        return textBox;
    }

    public UIForm getForm(String id) {
        form.setId(id);
        return form;
    }

    public UIButton getButton() {
        return button;
    }
}
