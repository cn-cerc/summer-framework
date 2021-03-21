package cn.cerc.ui.fields;

import cn.cerc.core.Record;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class CustomField extends AbstractField {

    public CustomField(UIComponent dataView, String name, int width) {
        super(dataView, name, width);
        this.setField("_selectCheckBox_");
    }

    @Override
    public String getText(Record record) {
        if (getBuildText() == null) {
            return "";
        }
        HtmlWriter html = new HtmlWriter();
        getBuildText().outputText(record, html);
        return html.toString();
    }

}
