package cn.cerc.ui.phone;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.ext.UISpan;

public class Block112 extends UIComponent {
    private UISpan left = new UISpan();
    private UISpan right = new UISpan();

    /**
     * 简单显示文字类信息，仅用于显示，不可修改
     * 
     * @param owner
     *            内容显示区
     */
    public Block112(UIComponent owner) {
        super(owner);
        left.setText("(leftText)");
        left.setRole("left");
        right.setText("(rightText)");
        right.setRole("right");
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<div class='block112' role='row'>");
        left.output(html);
        right.output(html);
        html.println("</div>");
    }

    public UISpan getLeft() {
        return left;
    }

    public UISpan getRight() {
        return right;
    }

    public Block112 setLeftText(String text) {
        left.setText(text);
        return this;
    }

    public Block112 setRightText(String text) {
        right.setText(text);
        return this;
    }

    public Block112 setRightText(String format, Object... args) {
        right.setText(String.format(format, args));
        return this;
    }
}
