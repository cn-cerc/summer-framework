package cn.cerc.ui.fields;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.Record;
import cn.cerc.mis.core.Application;
import cn.cerc.ui.SummerUI;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.other.BuildText;
import cn.cerc.ui.parts.UIComponent;

public class DateField extends AbstractField
        implements IFieldDialog, IFieldPattern, IFieldPlaceholder, IFieldRequired, IFieldAutofocus, IFieldBuildText {
    private static final ClassConfig config = new ClassConfig(DateField.class, SummerUI.ID);
    private DialogField dialog;
    private String pattern;
    private String placeholder;
    private boolean required;
    private boolean autofocus;
    private String icon;
    private BuildText buildText;

    public DateField(UIComponent owner, String name, String field) {
        super(owner, name, 5);
        this.setField(field);
        this.setDialog("showDateDialog");
        this.setIcon(Application.getStaticPath() + config.getClassProperty("icon", ""));
        this.setAlign("center");
    }

    public DateField(UIComponent owner, String name, String field, int width) {
        super(owner, name, width);
        this.setField(field);
        this.setDialog("showDateDialog");
        this.setIcon(Application.getStaticPath() + config.getClassProperty("icon", ""));
        this.setAlign("center");
    }

    @Override
    public FieldTitle createTitle() {
        FieldTitle title = super.createTitle();
        title.setType("date");
        return title;
    }

    @Override
    public String getText(Record record) {
        if (record == null) {
            return null;
        }
        if (getBuildText() != null) {
            HtmlWriter html = new HtmlWriter();
            getBuildText().outputText(record, html);
            return html.toString();
        }
        if (record.hasValue(getField())) {
            return record.getDate(getField()).getDate();
        } else {
            return "";
        }
    }

    @Override
    public DialogField getDialog() {
        return dialog;
    }

    @Override
    public DateField setDialog(String dialogfun) {
        this.dialog = new DialogField(dialogfun);
        dialog.setInputId(this.getId());
        return this;
    }

    @Override
    public String getPattern() {
        return this.pattern;
    }

    @Override
    public DateField setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    @Override
    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    public DateField setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public DateField setRequired(boolean required) {
        this.required = required;
        return this;
    }

    @Override
    public boolean isAutofocus() {
        return autofocus;
    }

    @Override
    public DateField setAutofocus(boolean autofocus) {
        this.autofocus = autofocus;
        return this;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public DateField setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    @Override
    public DateField createText(BuildText buildText) {
        this.buildText = buildText;
        return this;
    }

    @Override
    public BuildText getBuildText() {
        return buildText;
    }
}
