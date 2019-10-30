package cn.cerc.ui.phone;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.UrlRecord;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UIImage;

/**
 * 
 * @author 张弓
 *
 */
public class Block302 extends UIComponent {
    private String title = "(title)";
    private String describe = "(describe)";
    private UIImage rightIcon = new UIImage();
    private UrlRecord url;

    /**
     * 用于显示会员资料
     * 
     * @param owner
     *            内容显示区
     */
    public Block302(UIComponent owner) {
        super(owner);
        url = new UrlRecord();
        rightIcon.setSrc("jui/phone/block301-rightIcon.png");
        rightIcon.setRole("right");
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<div class='block302'>");
        html.print("<a href='%s'>", url.getUrl());
        html.print("<div>");
        html.print("<div role='title'>");
        html.print("<span role='title'>%s</span>", this.title);
        rightIcon.output(html);
        html.print("</div>");
        html.print("<div role='describe'>%s</div>", this.describe);
        html.print("</div>");
        html.print("</a>");
        html.print("<div style='clear: both'></div>");
        html.println("</div>");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public UrlRecord getUrl() {
        return url;
    }

    public void setUrl(UrlRecord url) {
        this.url = url;
    }

    public UIImage getRightIcon() {
        return rightIcon;
    }
}
