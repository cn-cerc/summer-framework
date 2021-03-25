package cn.cerc.ui.fields;

import java.text.DecimalFormat;

import cn.cerc.core.Record;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IFormatColumn;
import cn.cerc.ui.core.UrlRecord;
import cn.cerc.ui.fields.editor.ColumnEditor;
import cn.cerc.ui.grid.lines.AbstractGridLine;
import cn.cerc.ui.other.BuildText;
import cn.cerc.ui.other.BuildUrl;
import cn.cerc.ui.parts.UIComponent;

public class DoubleField extends AbstractField
        implements IFormatColumn, IFieldPattern, IFieldPlaceholder, IFieldAutofocus, IFieldBuildText, IFieldBuildUrl {
    private ColumnEditor editor;
    private String format = "0.####";
    private String pattern;
    private String placeholder;
    private boolean autofocus;
    private BuildText buildText;
    private BuildUrl buildUrl;

    public DoubleField(UIComponent owner, String title, String field) {
        super(owner, title, 4);
        this.setField(field);
        this.setAlign("right");
    }

    public DoubleField(UIComponent owner, String title, String field, int width) {
        super(owner, title, width);
        this.setField(field);
        this.setAlign("right");
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
        try {
            double val = record.getDouble(field);
            DecimalFormat df = new DecimalFormat(format);
            return df.format(val);
        } catch (NumberFormatException e) {
            return "0";
        }
    }

    @Override
    public FieldTitle createTitle() {
        FieldTitle title = super.createTitle();
        title.setType("float");
        return title;
    }

    @Override
    public String format(Record value) {
        Record ds = value;
        if (this.isReadonly()) {
            if (getBuildUrl() != null) {
                HtmlWriter html = new HtmlWriter();
                UrlRecord url = new UrlRecord();
                getBuildUrl().buildUrl(ds, url);
                if (!"".equals(url.getUrl())) {
                    html.print("<a href=\"%s\"", url.getUrl());
                    if (url.getTitle() != null) {
                        html.print(" title=\"%s\"", url.getTitle());
                    }
                    if (url.getTarget() != null) {
                        html.print(" target=\"%s\"", url.getTarget());
                    }
                    html.println(">%s</a>", getText(ds));
                } else {
                    html.println(getText(ds));
                }
                return html.toString();
            } else {
                return getText(ds);
            }
        }
        if (!(this.getOwner() instanceof AbstractGridLine)) {
            return getText(ds);
        }

        return getEditor().format(ds);
    }

    public ColumnEditor getEditor() {
        if (editor == null) {
            editor = new ColumnEditor(this);
        }
        return editor;
    }

    public String getFormat() {
        return format;
    }

    public DoubleField setFormat(String format) {
        this.format = format;
        return this;
    }

    @Override
    public String getPattern() {
        return this.pattern;
    }

    @Override
    public DoubleField setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    @Override
    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    public DoubleField setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    @Override
    public boolean isAutofocus() {
        return autofocus;
    }

    @Override
    public DoubleField setAutofocus(boolean autofocus) {
        this.autofocus = autofocus;
        return this;
    }

    @Override
    public DoubleField createText(BuildText buildText) {
        this.buildText = buildText;
        return this;
    }

    @Override
    public BuildText getBuildText() {
        return buildText;
    }

    @Override
    public DoubleField createUrl(BuildUrl buildUrl) {
        this.buildUrl = buildUrl;
        return this;
    }

    @Override
    public BuildUrl getBuildUrl() {
        return buildUrl;
    }
}
