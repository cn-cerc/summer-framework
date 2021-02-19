package cn.cerc.ui.phone;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UIImage;

/**
 * 
 * @author 张弓
 *
 */
public class Block603 extends UIComponent {
    private UIImage image = new UIImage();

    /**
     * 单图片显示
     * 
     * @param owner
     *            内容显示区
     */
    public Block603(UIComponent owner) {
        super(owner);
        image.setAlt("(image)");
        image.setSrc("jui/phone/block603_image.png");
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.println("<div class=\"block603\">");
        image.output(html);
        html.println("</div>");
    }

    public UIImage getImage() {
        return image;
    }

}
