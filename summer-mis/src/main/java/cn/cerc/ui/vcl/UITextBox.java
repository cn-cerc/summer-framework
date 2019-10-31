package cn.cerc.ui.vcl;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.ext.UISpan;

/**
 * 
 * 单行文本输入框
 * 
 * @author 张弓
 *
 */
public class UITextBox extends UIComponent {
    private UISpan caption;
    private String name;
    private String type;
    private String value;
    // 正则过滤
    protected String pattern;
    private String maxlength;
    private String placeholder;
    // 自动完成（默认为 off）
    private boolean autocomplete = false;
    private boolean autofocus;
    private boolean readonly;
    private boolean required;
    private String onclick;
    private String oninput;

    public UITextBox() {
        super();
    }

    public UITextBox(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        if (caption != null)
            caption.output(html);
        html.print("<input");
        if (this.getId() != null)
            html.print(" id='%s'", this.getId());
        if (this.name != null)
            html.print(" name='%s'", this.getName());
        if (type != null)
            html.print(" type=\"%s\"", type);
        if (maxlength != null)
            html.print(" maxlength=%s", this.maxlength);
        if (value != null)
            html.print(" value='%s'", this.value);
        if (pattern != null)
            html.print(" pattern=\"%s\"", this.pattern);
        if (onclick != null)
            html.print(" onclick='%s'", this.onclick);
        if (oninput != null)
            html.print(" oninput='%s'", this.oninput);
        if (placeholder != null)
            html.print(" placeholder='%s'", this.placeholder);

        if (this.autocomplete) {
            html.print(" autocomplete=\"on\"");
        } else {
            html.print(" autocomplete=\"off\"");
        }

        if (this.autofocus)
            html.print(" autofocus");
        if (this.required)
            html.print(" required");
        if (this.readonly)
            html.print(" readonly='readonly'");
        super.outputCss(html);
        html.println("/>");
    }

    public UISpan getCaption() {
        if (caption == null)
            caption = new UISpan();
        return caption;
    }

    public UITextBox setCaption(UISpan caption) {
        this.caption = caption;
        return this;
    }

    public String getName() {
        return name;
    }

    public UITextBox setName(String name) {
        this.name = name;
        return this;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public UITextBox setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public String getValue() {
        return value;
    }

    public UITextBox setValue(String value) {
        this.value = value;
        return this;
    }

    public String getPattern() {
        return pattern;
    }

    public UITextBox setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public UITextBox setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    public boolean isRequired() {
        return required;
    }

    public UITextBox setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public boolean isAutocomplete() {
        return autocomplete;
    }

    public UITextBox setAutocomplete(boolean autocomplete) {
        this.autocomplete = autocomplete;
        return this;
    }

    public boolean isAutofocus() {
        return autofocus;
    }

    public UITextBox setAutofocus(boolean autofocus) {
        this.autofocus = autofocus;
        return this;
    }

    public String getType() {
        return type;
    }

    public UITextBox setType(String type) {
        this.type = type;
        return this;
    }

    public String getOnclick() {
        return onclick;
    }

    public UITextBox setOnclick(String onclick) {
        this.onclick = onclick;
        return this;
    }

    public String getMaxlength() {
        return maxlength;
    }

    public UITextBox setMaxlength(String maxlength) {
        this.maxlength = maxlength;
        return this;
    }

    public String getOninput() {
        return oninput;
    }

    public UITextBox setOninput(String oninput) {
        this.oninput = oninput;
        return this;
    }

}
