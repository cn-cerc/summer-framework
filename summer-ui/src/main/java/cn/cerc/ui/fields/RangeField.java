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

public class RangeField extends AbstractField implements DataSource, IFieldDialog {
    private static final ClassResource res = new ClassResource(RangeField.class, SummerUI.ID);
    private UIDialogField dialog;

    public RangeField(UIComponent dataView, String name) {
        super(dataView, name, 0);
    }

    @Override
    public String getText() {
        Record record = getRecord();
        if (record != null) {
            if (this instanceof IFieldBuildText) {
                IFieldBuildText obj = (IFieldBuildText) this;
                if (obj.getBuildText() != null) {
                    HtmlWriter html = new HtmlWriter();
                    obj.getBuildText().outputText(record, html);
                    return html.toString();
                }
            }
            return record.getString(getField());
        } else {
            return null;
        }
    }

    @Override
    public void outputLine(HtmlWriter html) {
        if (this.isReadonly()) {
            html.print(this.getName() + "：");
            html.print(this.getText());
        } else {
            html.println("<label for=\"%s\">%s</label>", this.getId(), this.getName() + "：");
            AbstractField child = null;
            for (Component component : this.getComponents()) {
                if (component instanceof AbstractField) {
                    if (child != null) {
                        html.print("-");
                    }
                    child = (AbstractField) component;
                    String val = child.getCssClass();
                    child.setCssClass("price");
                    child.outputLine(html);
                    child.setCssClass(val);
                }
            }
            html.print("<span>");
            if (dialog != null && dialog.isOpen()) {
                dialog.setIcon("images/select-pic.png");
                dialog.setConfig(config).output(html);
            }
            html.print("</span>");
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
        return getDataSource().getDataSet();
    }

    @Override
    public void updateValue(String id, String code) {
        getDataSource().updateValue(id, code);
    }

    @Override
    public UIDialogField getDialog() {
        return dialog;
    }

    @Override
    public RangeField setDialog(String dialogfun) {
        this.dialog = new UIDialogField(dialogfun);
        dialog.setInputId(this.getId());
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

}
