package cn.cerc.ui.fields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import cn.cerc.core.Record;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class RadioField extends AbstractField {

    private final List<String> items = new ArrayList<>();

    public RadioField(UIComponent owner, String name, String field, int width) {
        super(owner, name, width);
        this.setField(field);
    }

    @Override
    public String getText() {
        Record record = getRecord();
        if (record == null) {
            return null;
        }
        int val = record.getInt(field);
        if (val < 0 || val > items.size() - 1) {
            return "" + val;
        }
        String result = items.get(val);
        if (result == null) {
            return "" + val;
        } else {
            return result;
        }
    }

    public RadioField add(String... items) {
        this.items.addAll(Arrays.asList(items));
        return this;
    }

    public RadioField add(Collection<String> items) {
        this.items.addAll(items);
        return this;
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
            if (getHtmType() != null) {
                html.print(" type=\"%s\"", this.getHtmType());
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
            if (this instanceof IFieldPlaceholder) {
                IFieldPlaceholder obj = (IFieldPlaceholder) this;
                if (obj.getPlaceholder() != null) {
                    html.print(" placeholder=\"%s\"", obj.getPlaceholder());
                }
            }
            if (this instanceof IFieldEvent) {
                IFieldEvent event = (IFieldEvent) this;
                if (event.getOninput() != null) {
                    html.print(" oninput=\"%s\"", event.getOninput());
                }
                if (event.getOnclick() != null) {
                    html.print(" onclick=\"%s\"", event.getOnclick());
                }
            }
            html.println("/>");

            html.print("<span>");
            html.println("</span>");
        }
    }

}
