package cn.cerc.ui.phone;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.UrlRecord;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UIImage;
import cn.cerc.ui.vcl.ext.UISpan;

/**
 * 标题
 * 
 * @author 郭向军
 *
 */
public class Block121 extends UIComponent {
    private UISpan title = new UISpan();
    private UIImage leftImage = new UIImage();
    private UrlRecord leftUrl = new UrlRecord();
    private UrlRecord rightUrl = new UrlRecord();
    private UISpan rightText = new UISpan();

    public Block121(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<header  class='block121'>");
        if (this.leftImage.getSrc() != null && !this.leftImage.getSrc().equals("")) {
            html.print("<a href='%s'  class='arrow-left'>", this.leftUrl.getUrl());
            this.leftImage.output(html);
            html.print("</a>");
        }
        html.print("<h1 class='title'>");
        this.title.output(html);
        html.print("</h1>");
        if (this.rightText.getText() != null && !this.rightText.getText().equals("")) {
            html.print("<a href='%s' class='arrow-right'>", this.rightUrl.getUrl());
            this.rightText.output(html);
            html.print("</a>");
        }
        html.print("</header>");
    }

    public UrlRecord getRightUrl() {
        return rightUrl;
    }

    public UISpan getRightText() {
        return rightText;
    }

    public UISpan getTitle() {
        return title;
    }

    public UIImage getLeftImage() {
        return this.leftImage;
    }

    public UrlRecord getLeftUrl() {
        return leftUrl;
    }

}
