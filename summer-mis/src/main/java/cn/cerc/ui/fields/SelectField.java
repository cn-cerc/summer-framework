package cn.cerc.ui.fields;

import java.util.LinkedHashMap;
import java.util.Map;

import cn.cerc.core.Record;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IColumn;
import cn.cerc.ui.parts.UIComponent;

/**
 * 列表下拉框组件
 */
public class SelectField extends AbstractField implements IColumn {
    private String trueText = "是";
    private String falseText = "否";
    private String title;
    private String onChange;
    private Map<String, String> items = new LinkedHashMap<>();

    public SelectField(UIComponent owner, String title, String field) {
        this(owner, title, field, 0);
    }

    public SelectField(UIComponent owner, String title, String field, int width) {
        super(owner, title, width);
        this.setField(field);
        this.setAlign("center");
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
        String val = dataSet.getString(field);
        if ("true".equals(val) || "false".equals(val)) {
            return Boolean.valueOf(val) ? trueText : falseText;
        }
        return val;
    }

    public void setBooleanText(String trueText, String falseText) {
        this.trueText = trueText;
        this.falseText = falseText;
    }

    @Override
    public void output(HtmlWriter html) {
        writeInput(html);
    }

    private String writeInput(HtmlWriter html) {
        html.print("<select name=\"%s\" role=\"%s\"", this.getId(), this.getField(), this.isReadonly());
        if (!this.isReadonly() && getOnChange() != null) {
            html.print(" onChange=\"%s\"", getOnChange());
        }
        if (this.isReadonly()) {
            html.print(" readonly='readonly' disabled='disabled'>");
        } else {
            html.print(">");
        }
        Record record = dataSource != null ? dataSource.getDataSet().getCurrent() : null;
        String current = record.getString(this.getField());
        for (String key : items.keySet()) {
            if (key.equals(current)) {
                html.print("<option value=\"%s\" selected>%s</option>", key, items.get(key));
            } else {
                html.print("<option value=\"%s\">%s</option>", key, items.get(key));
            }
        }
        html.print("</select>");
        return html.toString();
    }

    @Override
    public String getTitle() {
        return title == null ? this.getName() : title;
    }

    public SelectField setTitle(String title) {
        this.title = title;
        return this;
    }

    public void add(String key, String value) {
        items.put(key, value);
    }

    public void remove(String key) {
        items.remove(key);
    }

    public void copyValue(Map<String, String> data) {
        items.putAll(data);
    }

    @Override
    public String format(Object value) {
        HtmlWriter html = new HtmlWriter();
        return writeInput(html);
    }

    public String getOnChange() {
        return onChange;
    }

    public void setOnChange(String onChange) {
        this.onChange = onChange;
    }
}
