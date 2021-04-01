package cn.cerc.ui.columns;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.cerc.core.Record;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class CheckboxesColumn extends AbstractColumn implements IArrayColumn {

    private Map<String, String> items = new LinkedHashMap<>();
    private boolean hidden;
    private boolean readonly;

    public CheckboxesColumn(UIComponent owner) {
        super(owner);
    }

    public CheckboxesColumn(UIComponent owner, String name, String code) {
        super(owner);
        this.setName(name).setCode(code);
    }

    public CheckboxesColumn(UIComponent owner, String name, String code, int width) {
        super(owner);
        this.setName(name).setCode(code).setSpaceWidth(width);
    }

    public CheckboxesColumn put(String key, String text) {
        items.put(key, text);
        return this;
    }

    public CheckboxesColumn copyValues(Map<String, String> items) {
        for (String key : items.keySet()) {
            this.put(key, items.get(key));
        }
        return this;
    }

    public String getText(Record record) {
        if (record == null) {
            return "";
        }
        return record.getString(this.getCode());
    }

    @Override
    public void outputCell(HtmlWriter html) {
        html.print("Not support this output mode");
    }

    @Override
    public void outputLine(HtmlWriter html) {
        List<String> current = Arrays.asList(this.getText(getRecord()).split(","));
        html.println("<label>%s</label>", this.getName() + "：");
        html.println("<div>");
        int i = 0;
        for (String key : items.keySet()) {
            i++;
            String id = this.getId() + i;
            String value = items.get(key);
            html.print("<div>");
            html.println("<input type='checkbox' id='%s' name='%s'", id, this.getCode());
            html.println(" value='%s'", key);
            if (current.indexOf(key) > -1) {
                html.println(" checked='checked'");
            }
            html.print(">");
            html.println("<label for=\"%s\">%s</label>", id, value);
            html.print("</div>");
        }
        html.println("</div>");
    }

    @Override
    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public CheckboxesColumn setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public CheckboxesColumn setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

}
