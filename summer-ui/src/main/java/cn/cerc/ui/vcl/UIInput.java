package cn.cerc.ui.vcl;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.INameOwner;
import cn.cerc.ui.parts.UIComponent;

public class UIInput extends UIComponent implements INameOwner {

    private String caption = "";
    private String name;

    private String value = "";

    public UIInput(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        if (this.caption != null)
            html.print(this.caption);

        html.print("<input");

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
        html.println("/>");
    }

    public String getCaption() {
        return caption;
    }

    public UIInput setCaption(String caption) {
        this.caption = caption;
        return this;
    }

    @Override
    public String getName() {
        if (name == null)
            name = this.getId();
        return name;
    }

    public UIInput setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public UIInput setValue(String value) {
        this.value = value;
        return this;
    }

}
