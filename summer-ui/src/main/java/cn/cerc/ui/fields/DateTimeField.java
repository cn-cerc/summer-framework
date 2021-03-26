package cn.cerc.ui.fields;

import cn.cerc.core.Record;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class DateTimeField extends AbstractField {

    public DateTimeField(UIComponent owner, String name, String field) {
        super(owner, name, 10);
        this.setField(field);
        this.setAlign("center");
    }

    public DateTimeField(UIComponent owner, String name, String field, int width) {
        super(owner, name, width);
        this.setField(field);
    }

    @Override
    public String getText(Record record) {
        if (record == null) {
            return null;
        }
        if (buildText != null) {
            HtmlWriter html = new HtmlWriter();
            buildText.outputText(record, html);
            return html.toString();
        }
        return record.getString(getField());
    }
}
