package cn.cerc.ui.phone;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.UrlRecord;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UIImage;
import cn.cerc.ui.vcl.ext.UISpan;

public class Block101 extends UIComponent {
    private UISpan title = new UISpan();
    private UIImage image = new UIImage();
    private UrlRecord url = new UrlRecord();

    /**
     * 上游在线订货单手机版页面
     * 
     * @param owner
     *            所在内容显示区
     */
    public Block101(UIComponent owner) {
        super(owner);
        title.setText("(title)");
        image.setSrc("jui/phone/block101-go.png");
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<div class='block101' role='row'>");
        html.print("<div role='title'>");
        title.output(html);
        html.print("</div>");
        html.print("<a href='%s'>", url.getUrl());
        image.output(html);
        html.println("</a>");
        html.println("</div>");
    }

    public UISpan getTitle() {
        return title;
    }

    public UIImage getImage() {
        return image;
    }

    public UrlRecord getUrl() {
        return url;
    }
}
