package cn.cerc.ui.phone;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

/**
 * 
 * @author 张弓
 *
 */
public class Block103 extends UIComponent {
    /**
     * 显示商品详情，方便加入购物车
     * 
     * @param owner
     *            内容显示区
     */
    public Block103(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<div class='block103'>");
        html.println("</div>");
    }
}
