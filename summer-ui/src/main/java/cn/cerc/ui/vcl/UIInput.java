package cn.cerc.ui.vcl;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class UIInput extends UIComponent {

    private String caption;
    private String key;

    private String value;

    public UIInput(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        html.print(String.format("%s<input name=\"%s\" value=\"%s\"/>", this.caption, this.key, value));
    }

    public String getCaption() {
        return caption;
    }

    public UIInput setCaption(String caption) {
        this.caption = caption;
        return this;
    }

    public String getKey() {
        return key;
    }

    public UIInput setKey(String key) {
        this.key = key;
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
