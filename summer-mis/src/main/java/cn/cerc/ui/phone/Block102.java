package cn.cerc.ui.phone;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UIImage;
import cn.cerc.ui.vcl.ext.UISpan;

/**
 * @author 善贵
 *
 */
public class Block102 extends UIComponent {
    private UISpan title = new UISpan();
    private UIImage image = new UIImage();
    private String onclick;

    /**
     * 分段标题，带一个Go图标
     * 
     * @param owner
     *            内容显示区
     */
    public Block102(UIComponent owner) {
        super(owner);
        title.setText("(title)");
        image.setRole("image");
        onclick = "block102Fold(this)";
        image.setSrc("jui/phone/block102-expand.png");
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<div class='block102'");
        html.print(" onclick=\"%s\"", onclick);
        html.print(">");
        title.output(html);
        image.output(html);
        html.println("</div>");
    }

    public UISpan getTitle() {
        return title;
    }

    public UIImage getImage() {
        return image;
    }

    public String getOnclick() {
        return onclick;
    }

    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }
}
