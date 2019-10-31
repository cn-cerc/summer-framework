package cn.cerc.ui.vcl;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class UIButton extends UIComponent {
    private String name;
    private String value;
    private String text;
    private String onclick;
    private String role;

    public UIButton() {
        super();
    }

    public UIButton(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        html.print("<button ");
        if (getId() != null) {
            html.print(String.format(" id=\"%s\"", getId()));
        }
        if (name != null) {
            html.print(String.format(" name=\"%s\"", name));
        }
        if (value != null) {
            html.print(String.format(" value=\"%s\"", value));
        }
        if (role != null) {
            html.print(" role='%s'", this.role);
        }
        if (onclick != null) {
            html.print(String.format(" onclick=\"%s\"", onclick));
        }
        super.outputCss(html);
        html.print(">");
        html.print(text);
        html.println("</button>");
    }

    public String getText() {
        return text;
    }

    public UIButton setText(String text) {
        this.text = text;
        return this;
    }

    public String getName() {
        return name;
    }

    public UIButton setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public UIButton setValue(String value) {
        this.value = value;
        return this;
    }

    public String getOnclick() {
        return onclick;
    }

    public UIButton setOnclick(String onclick) {
        this.onclick = onclick;
        return this;
    }

    public UIButton setClickUrl(String url) {
        this.setOnclick(String.format("location.href='%s'", url));
        return this;
    }

    public String getRole() {
        return role;
    }

    public UIButton setRole(String role) {
        this.role = role;
        return this;
    }

}
