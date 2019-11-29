package cn.cerc.ui.phone;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.ext.UISpan;

public class Block106 extends UIComponent {
    private UISpan content = new UISpan();

    /**
     * 简单显示文字类信息，仅用于显示，不可修改
     * 
     * @param owner
     *            内容显示区
     */
    public Block106(UIComponent owner) {
        super(owner);
        content.setText("(content)");
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<div class='block106' role='row'>");
        content.output(html);
        html.println("</div>");
    }

    public UISpan getContent() {
        return content;
    }

}
