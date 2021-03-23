package cn.cerc.ui.fields;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.Record;
import cn.cerc.ui.SummerUI;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.other.BuildText;
import cn.cerc.ui.parts.UIComponent;

public class CodeNameField extends AbstractField
        implements IFieldDialog, IFieldPlaceholder, IFieldRequired, IFieldAutofocus, IFieldShowStar, IFieldBuildText {
    private static final ClassConfig config = new ClassConfig(CodeNameField.class, SummerUI.ID);
    private String nameField;
    private String placeholder;
    private DialogField dialog;
    private boolean required;
    private boolean autofocus;
    private boolean showStar;
    private BuildText buildText;

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
            if (dialog != null && dialog.isOpen()) {
                dialog.setConfig(config).output(html);
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
        if (nameField != null) {
            return nameField;
        }
        return this.getField() + "_name";
    }

    public CodeNameField setNameField(String nameField) {
        this.nameField = nameField;
        return this;
    }

    @Override
    public DialogField getDialog() {
        return dialog;
    }

    @Override
    public CodeNameField setDialog(String dialogfun) {
        this.dialog = new DialogField(dialogfun);
        dialog.setInputId(this.getId());
        return this;
    }

    @Override
    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    public CodeNameField setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public CodeNameField setRequired(boolean required) {
        this.required = required;
        return this;
    }

    @Override
    public boolean isAutofocus() {
        return autofocus;
    }

    @Override
    public CodeNameField setAutofocus(boolean autofocus) {
        this.autofocus = autofocus;
        return this;
    }

    @Override
    public boolean isShowStar() {
        return showStar;
    }

    @Override
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

}
