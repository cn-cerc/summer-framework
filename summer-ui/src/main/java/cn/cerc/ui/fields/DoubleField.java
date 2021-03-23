package cn.cerc.ui.fields;

import java.text.DecimalFormat;

import cn.cerc.core.Record;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IColumn;
import cn.cerc.ui.core.UrlRecord;
import cn.cerc.ui.fields.editor.ColumnEditor;
import cn.cerc.ui.grid.lines.AbstractGridLine;
import cn.cerc.ui.other.BuildText;
import cn.cerc.ui.other.BuildUrl;
import cn.cerc.ui.parts.UIComponent;

public class DoubleField extends AbstractField implements IColumn, IFieldBuildText, IFieldBuildUrl {
    private ColumnEditor editor;
    private String format = "0.####";
    private String pattern;
    private String placeholder;
    private boolean autofocus;
    private BuildText buildText;
    private BuildUrl buildUrl;
    private String inputType;

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
    public String getText() {
        Record record = getRecord();
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

    public String format(Record record) {
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
                    html.println(">%s</a>", getText());
                } else {
                    html.println(getText());
                }
                return html.toString();
            } else {
                return getText();
            }
        }
        if (!(this.getOwner() instanceof AbstractGridLine)) {
            return getText();
        }

        return getEditor().format(record);
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

    public String getPattern() {
        return this.pattern;
    }

    public DoubleField setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public DoubleField setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public boolean isAutofocus() {
        return autofocus;
    }

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

    // 隐藏输出
    @Override
    public void outputHidden(HtmlWriter html) {
        html.print("<input");
        html.print(" type=\"hidden\"");
        html.print(" id=\"%s\"", this.getId());
        html.print(" name=\"%s\"", this.getId());
        String value = this.getText();
        if (value != null) {
            html.print(" value=\"%s\"", value);
        }
        html.println("/>");
    }

    @Override
    public void outputLine(HtmlWriter html) {
        if (this.isReadonly()) {
            html.print(this.getName() + "：");
            html.print(this.getText());
        } else {
            html.print("<label for=\"%s\">%s</label>", this.getId(), this.getName() + "：");
            html.print("<input");
            if (getInputType() != null) {
                html.print(" type=\"%s\"", this.getInputType());
            } else {
                html.print(" type=\"text\"");
            }
            html.print(" id=\"%s\"", this.getId());
            html.print(" name=\"%s\"", this.getId());
            String value = this.getText();
            if (value != null) {
                html.print(" value=\"%s\"", value);
            }
            if (this.getValue() != null) {
                html.print(" value=\"%s\"", this.getValue());
            }
            if (this.isReadonly()) {
                html.print(" readonly=\"readonly\"");
            }
            if (this.getCssClass() != null) {
                html.print(" class=\"%s\"", this.getCssClass());
            }
            if (this.isAutofocus()) {
                html.print(" autofocus");
            }
            if (this.getPlaceholder() != null) {
                html.print(" placeholder=\"%s\"", this.getPlaceholder());
            }
            if (this.getPattern() != null) {
                html.print(" pattern=\"%s\"", this.getPattern());
            }

            html.println("/>");

            html.print("<span>");
            html.println("</span>");
        }
    }

    @Override
    public void outputColumn(HtmlWriter html) {
        // FIXME: 此处需要继续重构
        html.print(format(getRecord()));
    }

    public String getInputType() {
        return inputType;
    }

    public DoubleField setInputType(String inputType) {
        this.inputType = inputType;
        return this;
    }

    @Deprecated
    public DoubleField setHtmType(String htmType) {
        this.inputType = htmType;
        return this;
    }

}
