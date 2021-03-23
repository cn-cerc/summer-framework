package cn.cerc.ui.fields;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.Record;
import cn.cerc.ui.SummerUI;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.UIOriginComponent;
import cn.cerc.ui.other.BuildText;
import cn.cerc.ui.parts.UIComponent;

public class CodeNameField extends AbstractField implements IFieldBuildText {
    private static final ClassConfig config = new ClassConfig(CodeNameField.class, SummerUI.ID);
    private String nameField;
    private String placeholder;
    private UIDialogField dialog;
    private boolean required;
    private boolean autofocus;
    private boolean showStar;
    private BuildText buildText;
    private UIComponent helper;

    public CodeNameField(UIComponent owner, String name, String field) {
        super(owner, name, 0);
        this.setField(field);
    }

    @Override
    public void updateField() {
        if (getDataSource() != null) {
            getDataSource().updateValue(this.getId(), this.getField());
            getDataSource().updateValue(getNameField(), getNameField());
        }
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
    public void outputHidden(HtmlWriter html) {
        html.print("<input");
        html.print(" type=\"hidden\"");
        html.print(" name=\"%s\"", this.getId());
        html.print(" id=\"%s\"", this.getId());
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
            html.println("<label for=\"%s\">%s</label>", this.getId(), this.getName() + "：");

            html.print("<input");
            html.print(" type=\"hidden\"");
            html.print(" name=\"%s\"", this.getId());
            html.print(" id=\"%s\"", this.getId());
            String codeValue = this.getText();
            if (codeValue != null) {
                html.print(" value=\"%s\"", codeValue);
            }
            html.println("/>");

            html.print("<input");
            html.print(" type=\"text\"");
            html.print(" name=\"%s\"", getNameField());
            html.print(" id=\"%s\"", getNameField());
            String nameValue = null;

            Record record = getRecord();
            if (record != null) {
                nameValue = record.getString(getNameField());
                if (nameValue != null) {
                    html.print(" value=\"%s\"", nameValue);
                }
            }

            if (this.isReadonly()) {
                html.print(" readonly=\"readonly\"");
            }
            if (this.isAutofocus()) {
                html.print(" autofocus");
            }
            if (this.isRequired()) {
                html.print(" required");
            }
            if (this.getPlaceholder() != null) {
                html.print(" placeholder=\"%s\"", this.getPlaceholder());
            }
            html.println("/>");

            if (this.isShowStar()) {
                html.println("<font>*</font>");
            }

            html.print("<span>");
            if (helper != null)
                helper.output(html);
            html.print("</span>");
        }
    }

    public String getNameField() {
        if (nameField != null) {
            return nameField;
        }
        return this.getField() + "_name";
    }

    public CodeNameField setNameField(String nameField) {
        this.nameField = nameField;
        return this;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public CodeNameField setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public boolean isRequired() {
        return required;
    }

    public CodeNameField setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public boolean isAutofocus() {
        return autofocus;
    }

    public CodeNameField setAutofocus(boolean autofocus) {
        this.autofocus = autofocus;
        return this;
    }

    public boolean isShowStar() {
        return showStar;
    }

    public CodeNameField setShowStar(boolean showStar) {
        this.showStar = showStar;
        return this;
    }

    @Override
    public CodeNameField createText(BuildText buildText) {
        this.buildText = buildText;
        return this;
    }

    @Override
    public BuildText getBuildText() {
        return buildText;
    }

    public UIDialogField getDialog() {
        return dialog;
    }

    public CodeNameField setDialog(String dialogfunc) {
        this.dialog = new UIDialogField(getHelper());
        dialog.setDialogFunc(dialogfunc);
        dialog.setInputId(this.getId());
        dialog.setConfig(config);
        return this;
    }

    @Deprecated
    public CodeNameField setDialog(String dialogfun, String[] params) {
        setDialog(dialogfun);

        for (String string : params) {
            dialog.add(string);
        }

        return this;
    }

    public UIComponent getHelper() {
        if (helper == null)
            helper = new UIOriginComponent(this);
        return helper;
    }

}
