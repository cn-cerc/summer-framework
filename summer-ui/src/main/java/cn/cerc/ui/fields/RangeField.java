package cn.cerc.ui.fields;

import cn.cerc.core.ClassResource;
import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import cn.cerc.ui.SummerUI;
import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.DataSource;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IField;
import cn.cerc.ui.parts.UIComponent;

public class RangeField extends AbstractField implements DataSource {
    private static final ClassResource res = new ClassResource(RangeField.class, SummerUI.ID);

    public RangeField(UIComponent dataView, String name) {
        super(dataView, name, 0);
    }

    @Override
    public String getText(Record record) {
        return getDefaultText(record);
    }

    @Override
    public void output(HtmlWriter html) {
        Record record = dataSource != null ? dataSource.getDataSet().getCurrent() : null;
        if (this.hidden) {
            html.print("<input");
            html.print(" type=\"hidden\"");
            html.print(" name=\"%s\"", this.getId());
            html.print(" id=\"%s\"", this.getId());
            String value = this.getText(record);
            if (value != null) {
                html.print(" value=\"%s\"", value);
            }
            html.println("/>");
        } else {
            html.println("<label for=\"%s\">%s</label>", this.getId(), this.getName() + "：");
            AbstractField child = null;
            for (Component component : this.getComponents()) {
                if (component instanceof AbstractField) {
                    if (child != null) {
                        html.print("-");
                    }
                    child = (AbstractField) component;
                    String val = child.getCSSClass_phone();
                    child.setCSSClass_phone("price");
                    child.outputInput(html, record);
                    child.setCSSClass_phone(val);
                }
            }
            if (this.dialog != null) {
                html.print("<span>");
                html.print("<a href=\"javascript:%s('%s')\">", this.dialog, this.getId());
                html.print("<img src=\"images/select-pic.png\">");
                html.print("</a>");
                html.print("</span>");
            } else {
                html.print("<span></span>");
            }
        }
    }

    @Override
    public void addField(IField field) {
        if (field instanceof Component) {
            this.addComponent((Component) field);
        } else {
            throw new RuntimeException(String.format(res.getString(1, "不支持的数据类型：%s"), field.getClass().getName()));
        }
    }

    @Override
    public boolean isReadonly() {
        return dataSource.isReadonly();
    }

    @Override
    public void updateField() {
        AbstractField child = null;
        for (Component component : this.getComponents()) {
            if (component instanceof AbstractField) {
                child = (AbstractField) component;
                child.updateField();
            }
        }
    }

    @Override
    public DataSet getDataSet() {
        return dataSource.getDataSet();
    }

    @Override
    public void updateValue(String id, String code) {
        dataSource.updateValue(id, code);
    }
}
