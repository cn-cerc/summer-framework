package cn.cerc.ui.fields;

import cn.cerc.core.Record;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IFormatColumn;
import cn.cerc.ui.core.UrlRecord;
import cn.cerc.ui.fields.editor.ColumnEditor;
import cn.cerc.ui.grid.lines.AbstractGridLine;
import cn.cerc.ui.parts.UIComponent;

public class StringField extends AbstractField implements IFormatColumn {
    private ColumnEditor editor;

    public StringField(UIComponent owner, String name, String field) {
        super(owner, name, 0);
        this.setField(field);
    }

    public StringField(UIComponent owner, String name, String field, int width) {
        super(owner, name, 0);
        this.setField(field);
        this.setWidth(width);
    }

    @Override
    public String getText(Record record) {
        return getDefaultText(record);
    }

    @Override
    public String format(Object value) {
        if (!(value instanceof Record)) {
            return value.toString();
        }

        Record record = (Record) value;
        String data = getDefaultText(record);

        if (this.isReadonly()) {
            if (buildUrl != null) {
                HtmlWriter html = new HtmlWriter();
                UrlRecord url = new UrlRecord();
                buildUrl.buildUrl(record, url);
                if (!"".equals(url.getUrl())) {
                    html.print("<a href=\"%s\"", url.getUrl());
                    if (url.getTitle() != null) {
                        html.print(" title=\"%s\"", url.getTitle());
                    }
                    if (url.getTarget() != null) {
                        html.print(" target=\"%s\"", url.getTarget());
                    }
                    html.println(">%s</a>", data);
                } else {
                    html.println(data);
                }
                return html.toString();
            } else {
                return data;
            }
        }

        if (!(this.getOwner() instanceof AbstractGridLine)) {
            return data;
        }

        return getEditor().format(record);
    }

    public ColumnEditor getEditor() {
        if (editor == null) {
            editor = new ColumnEditor(this);
        }
        return editor;
    }
}
