package cn.cerc.ui.phone;

import org.apache.commons.lang.StringUtils;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.UrlRecord;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UIImage;
import cn.cerc.ui.vcl.ext.UISpan;

/**
 * 两组左边图标右边文字
 * 
 * @author 郭向军
 *
 */
public class Block119 extends UIComponent {
    private UISpan leftTitle = new UISpan();
    private UIImage leftImage = new UIImage();
    private UISpan rightTitle = new UISpan();
    private UIImage rightImage = new UIImage();
    private UrlRecord leftUrl = new UrlRecord();
    private UrlRecord rightUrl = new UrlRecord();

    public Block119(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<div class='block119'>");
        html.print("<ul class='cf'>");
        html.print("<li>");
        html.print("<div class='item'>");
        html.print("<a href='%s'>", this.leftUrl.getUrl());
        this.leftImage.output(html);
        this.leftTitle.output(html);
        html.print("</a>");
        html.print("</div>");
        html.print("</li>");
        if (!StringUtils.isBlank(this.rightImage.getSrc()) && !StringUtils.isBlank(this.rightTitle.getText())) {
            html.print("<li>");
            html.print("<div class='item'>");
            html.print("<a href='%s'>", this.rightUrl.getUrl());
            this.rightImage.output(html);
            this.rightTitle.output(html);
            html.print("</a>");
            html.print("</div>");
            html.print("</li>");
        }
        html.print("</ul>");
        html.print("</div>");
    }

    public UrlRecord getLeftUrl() {
        return leftUrl;
    }

    public UrlRecord getRightUrl() {
        return rightUrl;
    }

    public UISpan getRightTitle() {
        return rightTitle;
    }

    public UISpan getLeftTitle() {
        return leftTitle;
    }

    public UIImage getLeftImage() {
        return leftImage;
    }

    public UIImage getRightImage() {
        return rightImage;
    }

}
