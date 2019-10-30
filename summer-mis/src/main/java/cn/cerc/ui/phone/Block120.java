package cn.cerc.ui.phone;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.UrlRecord;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UIImage;
import cn.cerc.ui.vcl.ext.UISpan;

/**
 * 部门管理
 * 
 * @author 郭向军
 *
 */
public class Block120 extends UIComponent {
    private UISpan title = new UISpan();
    private UIImage rightImage = new UIImage();
    private UIImage leftImage = new UIImage();
    private UISpan leftText = new UISpan();
    private UISpan rightText = new UISpan();
    private UrlRecord rightUrl = new UrlRecord();
    private UrlRecord leftUrl = new UrlRecord();

    public Block120(UIComponent owner) {
        super(owner);
        title.setText("item");
        leftText.setText("修改");
        rightText.setText("删除");
        rightImage.setSrc("jui/phone/block120_delete.png");
        leftImage.setSrc("jui/phone/block120_edit.png");
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<div class='block120'>");
        html.print("<ul><li>");
        html.print("<div class='m-left'>");
        this.title.output(html);
        html.print("</div>");
        html.print("<div class='m-right'>");
        html.print("<a href='%s'>", this.getLeftUrl().getUrl());
        this.leftImage.output(html);
        this.leftText.output(html);
        html.print("</a>");
        html.print("<a href='%s'>", this.getRightUrl().getUrl());
        this.rightImage.output(html);
        this.rightText.output(html);
        html.print("</a>");
        html.print("</div>");
        html.print("</div>");
    }

    public UISpan getTitle() {
        return title;
    }

    public UIImage getRightImage() {
        return rightImage;
    }

    public UIImage getLeftImage() {
        return leftImage;
    }

    public UISpan getLeftText() {
        return leftText;
    }

    public UISpan getRightText() {
        return rightText;
    }

    public UrlRecord getRightUrl() {
        return rightUrl;
    }

    public UrlRecord getLeftUrl() {
        return leftUrl;
    }

}
