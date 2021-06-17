package cn.cerc.ui.vcl;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.INameOwner;
import cn.cerc.ui.parts.UIComponent;

public class UIInput extends UIComponent implements INameOwner {

    public static final String TYPE_BUTTON = "button";
    public static final String TYPE_CHECKBOX = "checkbox";
    public static final String TYPE_RADIO = "radio";
    public static final String TYPE_PASSWORD = "password";
    public static final String TYPE_SUBMIT = "submit";
    public static final String TYPE_FILE = "file";
    public static final String TYPE_DATE = "date";
    public static final String TYPE_DATETIME_LOCAL = "datetime-local";
    public static final String TYPE_TEXT = "text";

    private String caption;
    private String name;
    private String value;
    private boolean readonly;
    private boolean required;
    private boolean hidden;
    private boolean checked;
    private String placeholder;
    private String inputType = TYPE_TEXT;

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

        if (this.isChecked()) {
            html.print(" checked");
        }
        // 以下为附加功能
        if (this.required) {
            html.print(" required");
        }
        if (inputType != null) {
            html.print(" type=\"%s\"", this.inputType);
            if (this.isChecked()) {
                html.print(" checked");
            }
        }
        if (placeholder != null) {
            html.print(" placeholder=\"%s\"", this.placeholder);
        }
        html.println(" />");
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

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

}
