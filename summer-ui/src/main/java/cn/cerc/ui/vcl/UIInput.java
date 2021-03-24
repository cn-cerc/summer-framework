package cn.cerc.ui.vcl;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.INameOwner;
import cn.cerc.ui.parts.UIComponent;

public class UIInput extends UIComponent implements INameOwner {
    private String caption;
    private String name;
    private String value;
    private boolean readonly;
    private boolean hidden;
    private String placeholder;
    private String inputType;

    public UIInput(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        if (this.caption != null) {
            html.print(this.caption);
        }
        html.print("<input");

        if (getId() != null) {
            html.print(String.format(" id=\"%s\"", getId()));
        }
        if (name != null) {
            html.print(String.format(" name=\"%s\"", name));
        } else if (this.getId() != null) {
            html.print(String.format(" name=\"%s\"", getId()));
        }
        if (this.readonly) {
            html.print(" readonly=\"readonly\"");
        }
        if (value != null) {
            html.print(String.format(" value=\"%s\"", value));
        }
        if (this.hidden) {
            html.println(" type=\"hidden\" />");
            return;
        }

        // 以下为附加功能

        if (inputType != null) {
            html.print(" type=\"%s\"", this.inputType);
        }
        if (placeholder != null) {
            html.print(" placeholder=\"%s\"", this.placeholder);
        }
        html.println("/>");
    }

    public String getCaption() {
        return caption;
    }

    @Deprecated
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

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

}
