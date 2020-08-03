package cn.cerc.ui.fields;

import cn.cerc.core.Record;
import cn.cerc.mis.cdn.CDN;
import cn.cerc.ui.UIConfig;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class DateField extends AbstractField {

    public DateField(UIComponent owner, String name, String field) {
        super(owner, name, 5);
        this.setField(field);
        this.setDialog("showDateDialog");
        this.setIcon(UIConfig.dateIcon);
        this.setAlign("center");
    }

    @Override
    public Title createTitle() {
        Title title = super.createTitle();
        title.setType("date");
        return title;
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
        if (record.hasValue(getField())) {
            return record.getDate(getField()).getDate();
        } else {
            return "";
        }
    }
}
