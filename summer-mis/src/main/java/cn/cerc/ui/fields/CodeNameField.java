package cn.cerc.ui.fields;

import cn.cerc.core.Record;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class CodeNameField extends AbstractField {
    private String nameField;

    public CodeNameField(UIComponent owner, String name, String field) {
        super(owner, name, 0);
        this.setField(field);
    }

    @Override
    public void updateField() {
        if (dataSource != null) {
            dataSource.updateValue(this.getId(), this.getField());
            dataSource.updateValue(getNameField(), getNameField());
        }
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
        return dataSet.getString(getField());
    }

    @Override
    public void output(HtmlWriter html) {
        Record record = dataSource != null ? dataSource.getDataSet().getCurrent() : null;
        if (this.isHidden()) {
            html.print("<input");
            html.print(" type=\"hidden\"");
            html.print(" name=\"%s\"", this.getId());
            html.print(" id=\"%s\"", this.getId());
            String value = this.getText(record);
            if (value != null)
                html.print(" value=\"%s\"", value);
            html.println("/>");
        } else {
            html.println("<label for=\"%s\">%s</label>", this.getId(), this.getName() + "：");

            html.print("<input");
            html.print(" type=\"hidden\"");
            html.print(" name=\"%s\"", this.getId());
            html.print(" id=\"%s\"", this.getId());
            String codeValue = this.getText(record);
            if (codeValue != null)
                html.print(" value=\"%s\"", codeValue);
            html.println("/>");

            html.print("<input");
            html.print(" type=\"text\"");
            html.print(" name=\"%s\"", getNameField());
            html.print(" id=\"%s\"", getNameField());
            String nameValue = null;
            if (record != null) {
                nameValue = record.getString(getNameField());
                if (nameValue != null)
                    html.print(" value=\"%s\"", nameValue);
            }
            if (this.isReadonly())
                html.print(" readonly=\"readonly\"");
            if (this.isAutofocus())
                html.print(" autofocus");
            if (this.isRequired())
                html.print(" required");
            if (this.getPlaceholder() != null)
                html.print(" placeholder=\"%s\"", this.getPlaceholder());
            html.println("/>");

            if (this.isShowStar()) {
                html.println("<font>*</font>");
            }

            html.print("<span>");
            if (this.getDialog() != null && this.getDialog().isOpen()) {
                html.print("<a href=\"%s\">", getUrl(this.getDialog()));
                html.print("<img src=\"images/searchIocn.png\">");
                html.print("</a>");
            }
            html.print("</span>");
        }
    }

    public String getUrl(DialogField dialog) {
        if (dialog.getDialogfun() == null) {
            throw new RuntimeException("dialogfun is null");
        }

        StringBuilder build = new StringBuilder();
        build.append("javascript:");
        build.append(dialog.getDialogfun());
        build.append("(");

        build.append("'");
        build.append(getId());
        build.append(",");
        build.append(getNameField());
        build.append("'");

        if (dialog.getParams().size() > 0) {
            build.append(",");
        }

        int i = 0;
        for (String param : dialog.getParams()) {
            build.append("'");
            build.append(param);
            build.append("'");
            if (i != dialog.getParams().size() - 1) {
                build.append(",");
            }
            i++;
        }
        build.append(")");

        return build.toString();
    }

    public String getNameField() {
        if (nameField != null)
            return nameField;
        return this.getField() + "_name";
    }

    public CodeNameField setNameField(String nameField) {
        this.nameField = nameField;
        return this;
    }

}
