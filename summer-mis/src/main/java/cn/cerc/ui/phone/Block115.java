package cn.cerc.ui.phone;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UIImage;
import cn.cerc.ui.vcl.ext.UISpan;

/**
 * @author 善贵
 *
 */
public class Block115 extends UIComponent {
    private UISpan title = new UISpan();
    private UIImage image = new UIImage();

    /**
     * 分段标题，带一个图标
     * 
     * @param owner
     *            内容显示区
     */
    public Block115(UIComponent owner) {
        super(owner);
        title.setText("(title)");
        image.setRole("image");
        image.setSrc("jui/phone/block401_004.png");
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<div class='block115'>");
        image.output(html);
        title.output(html);
        html.println("</div>");
    }

    public UISpan getTitle() {
        return title;
    }

    public UIImage getImage() {
        return image;
    }
}
