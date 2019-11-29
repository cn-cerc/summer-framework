package cn.cerc.ui.phone;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UIButton;

/**
 * 
 * @author 张弓
 *
 */
public class Block992 extends UIComponent {
    private List<UIButton> items = new ArrayList<>();

    /**
     * 底部状态栏：1个功能按钮+提示文字
     * 
     * @param owner
     *            内容显示区
     */
    public Block992(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.println("<div class=\"block992\">");
        for (UIButton button : items)
            button.output(html);
        html.println("</div>");
    }

    public UIButton addButton(String caption) {
        UIButton button = new UIButton();
        button.setText(caption);
        items.add(button);
        return button;
    }
}
