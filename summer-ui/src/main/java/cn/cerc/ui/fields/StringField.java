package cn.cerc.ui.fields;

import cn.cerc.core.Record;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IColumn;
import cn.cerc.ui.core.UrlRecord;
import cn.cerc.ui.fields.editor.ColumnEditor;
import cn.cerc.ui.grid.lines.AbstractGridLine;
import cn.cerc.ui.parts.UIComponent;

public class StringField extends AbstractField
        implements IColumn, IFieldDialog, IFieldPlaceholder, IFieldPattern, IFieldRequired, IFieldAutofocus {
    private ColumnEditor editor;
    private DialogField dialog;
    private String placeholder;
    private String pattern;
    private boolean required;
    private boolean autofocus;
    private String icon;

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
            if (getBuildUrl() != null) {
                HtmlWriter html = new HtmlWriter();
                UrlRecord url = new UrlRecord();
                getBuildUrl().buildUrl(record, url);
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

    @Override
    public DialogField getDialog() {
        return dialog;
    }

    @Override
    public AbstractField setDialog(String dialogfun) {
        this.dialog = new DialogField(dialogfun);
        dialog.setInputId(this.getId());
        return this;
    }

    @Override
    public AbstractField setDialog(String dialogfun, String... params) {
        this.dialog = new DialogField(dialogfun);
        dialog.setInputId(this.getId());
        for (String string : params) {
            this.dialog.add(string);
        }
        return this;
    }

    @Override
    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    public StringField setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    @Override
    public String getPattern() {
        return pattern;
    }

    @Override
    public StringField setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public StringField setRequired(boolean required) {
        this.required = required;
        return this;
    }

    @Override
    public boolean isAutofocus() {
        return autofocus;
    }

    @Override
    public StringField setAutofocus(boolean autofocus) {
        this.autofocus = autofocus;
        return this;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public StringField setIcon(String icon) {
        this.icon = icon;
        return this;
    }
}
