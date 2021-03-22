package cn.cerc.ui.fields;

import cn.cerc.core.Record;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.other.BuildText;
import cn.cerc.ui.parts.UIComponent;

public class ButtonField extends AbstractField implements IFieldEvent, IFieldBuildText {
    private String data;
    private String type;
    private String onclick;
    private String oninput;
    private BuildText buildText;

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
    public String getText(Record record) {
        if (record == null) {
            return null;
        }
        if (getBuildText() != null) {
            HtmlWriter html = new HtmlWriter();
            getBuildText().outputText(record, html);
            return html.toString();
        }
        return record.getString(getField());
    }

    @Override
    public void outputReadonly(HtmlWriter html, Record record) {
        outputDefault(html, record);
    }

    @Override
    public void outputDefault(HtmlWriter html, Record record) {
        html.print("<button name=\"%s\"", this.getId());
        if (this.data != null) {
            html.print(" value=\"%s\"", this.data);
        }
        if (this.getCssClass() != null) {
            html.print(" class=\"%s\"", this.getCssClass());
        }
        if (this.getOnclick() != null) {
            html.print(" onclick=\"%s\"", this.getOnclick());
        }
        if (this.type != null) {
            html.print(" type=\"%s\"", this.type);
        }
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

    @Override
    public String getOninput() {
        return oninput;
    }

    @Override
    public ButtonField setOninput(String oninput) {
        this.oninput = oninput;
        return this;
    }

    @Override
    public String getOnclick() {
        return onclick;
    }

    @Override
    public ButtonField setOnclick(String onclick) {
        this.onclick = onclick;
        return this;
    }

    @Override
    public ButtonField createText(BuildText buildText) {
        this.buildText = buildText;
        return this;
    }

    @Override
    public BuildText getBuildText() {
        return buildText;
    }

    // 隐藏输出
    @Override
    public void outputHidden(HtmlWriter html, Record record) {
        html.print("<input");
        html.print(" type=\"hidden\"");
        html.print(" id=\"%s\"", this.getId());
        html.print(" name=\"%s\"", this.getId());
        String value = this.getText(record);
        if (value != null) {
            html.print(" value=\"%s\"", value);
        }
        html.println("/>");
    }

}
