package cn.cerc.ui.fields;

import cn.cerc.core.Record;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class ButtonField extends AbstractField {
    private String data;
    private String type;

    public ButtonField() {
        super(null, null, 0);
    }

    public ButtonField(UIComponent owner, String name, String id, String data) {
        super(owner, name, 0);
        this.setField(id);
        this.data = data;
        this.setId(id);
    }

    public String getData() {
        return data;
    }

    public ButtonField setData(String data) {
        this.data = data;
        return this;
    }

    @Override
    public String getText(Record dataSet) {
        if (dataSet == null)
            return null;
        if (buildText != null) {
            HtmlWriter html = new HtmlWriter();
            buildText.outputText(dataSet, html);
            return html.toString();
        }
        return dataSet.getString(getField());
    }

    @Override
    public void output(HtmlWriter html) {
        html.print("<button name=\"%s\"", this.getId());
        if (this.data != null)
            html.print(" value=\"%s\"", this.data);
        if (getCSSClass_phone() != null)
            html.print(" class=\"%s\"", getCSSClass_phone());
        if (this.getOnclick() != null)
            html.print(" onclick=\"%s\"", this.getOnclick());
        if (this.type != null)
            html.print(" type=\"%s\"", this.type);
        html.print(">");
        html.print("%s</button>", this.getName());
    }

    public String getType() {
        return type;
    }

    public ButtonField setType(String type) {
        this.type = type;
        return this;
    }
}
