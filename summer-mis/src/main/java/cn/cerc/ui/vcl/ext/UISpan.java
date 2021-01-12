package cn.cerc.ui.vcl.ext;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class UISpan extends UIComponent {
    private String text;
    private String role;
    private String onclick;
    private String url;
    private String target = "_blank";

    public UISpan() {
        super();
    }

    public UISpan(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        html.print("<span");
        if (getId() != null) {
            html.print(" id='%s'", this.getId());
        }
        super.outputCss(html);
        if (role != null) {
            html.print(" role='%s'", this.role);
        }
        if (onclick != null) {
            html.print(" onclick='%s'", this.onclick);
        }
        html.print(">");

        if (this.url != null) {
            html.print("<a href='%s'", this.url);
            if (this.target != null) {
                html.print(" target='%s'", this.target);
            }
            html.print(">");
        }
        html.print(text);
        if (this.url != null) {
            html.println("</a>");
        }
        html.println("</span>");
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setText(String format, Object... args) {
        this.text = String.format(format, args);
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

    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
