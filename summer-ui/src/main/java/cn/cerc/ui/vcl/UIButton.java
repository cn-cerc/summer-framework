package cn.cerc.ui.vcl;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.INameOwner;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.parts.UICssComponent;

public class UIButton extends UICssComponent implements INameOwner {
    private String name;
    private String value;
    private String text;
    private String onclick;
    private String role;
    private String type;

    public UIButton() {
        super();
    }

    public UIButton(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        html.print("<button");
        if (getId() != null) {
            html.print(String.format(" id=\"%s\"", getId()));
        }
        if (name != null) {
            html.print(String.format(" name=\"%s\"", name));
        } else if (this.getId() != null) {
            html.print(String.format(" name=\"%s\"", getId()));
        }
        if (value != null) {
            html.print(String.format(" value=\"%s\"", value));
        }
        if (role != null) {
            html.print(" role='%s'", this.role);
        }
        if (type != null) {
            html.print(" type='%s'", this.type);
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

    @Override
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

    public String getType() {
        return type;
    }

    public UIButton setType(String type) {
        this.type = type;
        return this;
    }
}
