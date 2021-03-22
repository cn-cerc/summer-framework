package cn.cerc.ui.fields;

import java.util.LinkedHashMap;
import java.util.Map;

import cn.cerc.core.Record;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IOutoutLine;
import cn.cerc.ui.other.BuildText;
import cn.cerc.ui.parts.UIComponent;

public class OptionField extends AbstractField implements IFieldShowStar, IFieldBuildText, IOutoutLine {
    private Map<String, String> items = new LinkedHashMap<>();
    private String defaultValue;
    private int size;// 默认显示行数
    private boolean showStar;
    private BuildText buildText;

    public OptionField(UIComponent owner, String name, String field) {
        super(owner, name, 0);
        this.setField(field);
    }

    public OptionField(UIComponent owner, String name, String field, int width) {
        super(owner, name, width);
        this.setField(field);
    }

    @Deprecated
    public OptionField add(String key, String text) {
        return this.put(key, text);
    }

    public OptionField put(String key, String text) {
        if (this.defaultValue == null) {
            defaultValue = key;
        }
        items.put(key, text);
        return this;
    }

    public OptionField copyValues(Map<String, String> items) {
        for (String key : items.keySet()) {
            this.put(key, items.get(key));
        }
        return this;
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
        return record.getString(getField());
    }

    @Override
    public String getString() {
        String result = super.getString();
        if (result == null || "".equals(result)) {
            return this.defaultValue;
        }
        return result;
    }

    @Override
    public void outputReadonly(HtmlWriter html) {
        outputDefault(html);
    }

    @Override
    public void outputDefault(HtmlWriter html) {
        String current = this.getText();
        html.println("<label for=\"%s\">%s</label>", this.getId(), this.getName() + "：");
        html.print("<select id=\"%s\" name=\"%s\"", this.getId(), this.getId());
        if (this.size > 0) {
            html.print(" size=\"%s\"", this.getSize());
        }
        if (this.isReadonly()) {
            html.print(" disabled");
        }
        if (this.getCssStyle() != null) {
            html.print(" style=\"%s\"", this.getCssStyle());
        }
        html.print(">");
        for (String key : items.keySet()) {
            String value = items.get(key);
            html.print("<option value=\"%s\"", key);
            if (key.equals(current)) {
                html.print(" selected");
            }
            html.print(">");
            html.println(String.format("%s</option>", value));
        }
        html.println("</select>");
        if (this.isShowStar()) {
            html.print("<font>*</font>");
        }
        html.print("<span></span>");
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public boolean isShowStar() {
        return showStar;
    }

    @Override
    public OptionField setShowStar(boolean showStar) {
        this.showStar = showStar;
        return this;
    }

    @Override
    public OptionField createText(BuildText buildText) {
        this.buildText = buildText;
        return this;
    }

    @Override
    public BuildText getBuildText() {
        return buildText;
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

}