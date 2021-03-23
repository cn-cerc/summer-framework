package cn.cerc.ui.fields;

import cn.cerc.core.ClassResource;
import cn.cerc.core.Record;
import cn.cerc.ui.SummerUI;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IColumn;
import cn.cerc.ui.other.BuildText;
import cn.cerc.ui.parts.UIComponent;

public class ItField extends AbstractField implements IFieldBuildText, IColumn {

    private static final ClassResource res = new ClassResource(ItField.class, SummerUI.ID);
    private BuildText buildText;

    public ItField(UIComponent owner) {
        super(owner, res.getString(1, "序"), 2);
        this.setReadonly(true);
        this.setShortName("");
        this.setAlign("center");
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
        return "" + getDataSource().getDataSet().getRecNo();
    }

    @Override
    public String getField() {
        return "_it_";
    }

    @Override
    public FieldTitle createTitle() {
        FieldTitle title = super.createTitle();
        title.setType("int");
        return title;
    }

    @Override
    public ItField setReadonly(boolean readonly) {
        super.setReadonly(true);
        return this;
    }

    @Override
    public ItField createText(BuildText buildText) {
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

    @Override
    public void outputLine(HtmlWriter html) {
        if (this.isReadonly()) {
            html.print(this.getName() + "：");
            html.print(this.getText());
        } else {
            html.print("<label for=\"%s\">%s</label>", this.getId(), this.getName() + "：");
            html.print("<input");
            html.print(" type=\"text\"");
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
            html.println("/>");

            html.print("<span>");
            html.println("</span>");
        }
    }

    @Override
    public void outputColumn(HtmlWriter html) {
        html.print(this.getText());
    }

}
