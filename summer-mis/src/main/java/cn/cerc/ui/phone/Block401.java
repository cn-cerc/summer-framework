package cn.cerc.ui.phone;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UIButton;
import cn.cerc.ui.vcl.UIImage;
import cn.cerc.ui.vcl.ext.UISpan;

public class Block401 extends UIComponent {
    private String title = "(title)";
    private UIImage product = new UIImage();
    private List<UIImage> images = new ArrayList<>();
    private UISpan remark = new UISpan();
    private UISpan describe = new UISpan();
    private UIButton button = new UIButton();
    private String url;
    private String style;

    /**
     * 显示商品摘要，方便加入购物车
     * 
     * @param owner
     *            内容显示区
     */
    public Block401(UIComponent owner) {
        super(owner);
        product.setRole("product");
        product.setAlt("(product)");
        product.setSrc("jui/phone/block401-product.png");
        button.setText("(button)");

        remark.setRole("remark");
        remark.setText("(remark)");
        describe.setRole("describe");
        describe.setText("(describe)");
    }

    @Override
    public void output(HtmlWriter html) {
        html.println("<!-- %s -->", this.getClass().getName());
        html.print("<section class='block401'>");
        html.print("<div class='up_con'>");
        if (this.url != null && !"".equals(this.url)) {
            html.print("<a href=\"%s\" target=\"_blank\">", this.url);
        }
        product.output(html);
        if (this.url != null && !"".equals(this.url)) {
            html.print("</a>");
        }
        html.print("<div role='title'>%s</div>", this.title);
        html.print("<div role='operation' ");
        if (this.style != null && !"".equals(this.style)) {
            html.print("style='%s'>", this.style);
        } else {
            html.print(">");
        }

        for (UIImage image : images) {
            html.print("<span role='image'>");
            image.output(html);
            html.print("</span>");
        }

        describe.output(html);
        html.print("</div>");
        html.print("<div class='info'>");
        remark.output(html);
        button.output(html);
        html.print("</div>");
        html.print("</div>");
        html.println("</section>");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTitle(String format, Object... args) {
        this.title = String.format(format, args);
    }

    public UIButton getButton() {
        return button;
    }

    public void setButton(UIButton button) {
        this.button = button;
    }

    public void addImage(String imgUrl) {
        UIImage image = new UIImage();
        image.setSrc(imgUrl);
        images.add(image);
    }

    public UIImage getProduct() {
        return product;
    }

    public UISpan getRemark() {
        return remark;
    }

    public UISpan getDescribe() {
        return describe;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
