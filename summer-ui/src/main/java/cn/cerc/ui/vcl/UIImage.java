package cn.cerc.ui.vcl;

import cn.cerc.mis.core.Application;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.parts.UICssComponent;

public class UIImage extends UICssComponent {
    private String width;
    private String height;
    private String src;
    private String role;
    private String onclick;
    private String alt;

    public UIImage() {
        super();
    }

    public UIImage(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        html.print("<img src='%s'", Application.getStaticPath() + this.src);
        if (role != null) {
            html.print(" role='%s'", this.role);
        }
        if (alt != null) {
            html.print(" alt='%s'", this.alt);
        }
        if (width != null) {
            html.print(" width='%s'", this.width);
        }
        if (height != null) {
            html.print(" height='%s'", this.height);
        }
        if (onclick != null) {
            html.print(" onclick='%s'", this.onclick);
        }
        super.outputCss(html);
        html.println("/>");
    }

    public String getWidth() {
        return width;
    }

    public UIImage setWidth(String width) {
        this.width = width;
        return this;
    }

    public String getHeight() {
        return height;
    }

    public UIImage setHeight(String height) {
        this.height = height;
        return this;
    }

    public String getSrc() {
        return src;
    }

    public UIImage setSrc(String src) {
        this.src = src;
        return this;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getOnclick() {
        return onclick;
    }

    public UIImage setOnclick(String onclick) {
        this.onclick = onclick;
        return this;
    }

    public String getAlt() {
        return alt;
    }

    public UIImage setAlt(String alt) {
        this.alt = alt;
        return this;
    }
}
