package cn.cerc.ui.vcl.ext;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.parts.UICssComponent;

public class UISpan extends UICssComponent {
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

    public UISpan setText(String text) {
        this.text = text;
        return this;
    }

    public UISpan setText(String format, Object... args) {
        this.text = String.format(format, args);
        return this;
    }

    public String getRole() {
        return role;
    }

    public UISpan setRole(String role) {
        this.role = role;
        return this;
    }

    public String getOnclick() {
        return onclick;
    }

    public UISpan setOnclick(String onclick) {
        this.onclick = onclick;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public UISpan setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getTarget() {
        return target;
    }

    public UISpan setTarget(String target) {
        this.target = target;
        return this;
    }
}
