package cn.cerc.ui.fields;

import cn.cerc.core.ClassResource;
import cn.cerc.core.Record;
import cn.cerc.ui.SummerUI;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.other.BuildText;
import cn.cerc.ui.parts.UIComponent;

public class OperaField extends AbstractField implements IFieldDialog, IFieldBuildText {
    private static final ClassResource res = new ClassResource(OperaField.class, SummerUI.ID);

    private String value = res.getString(1, "内容");

    private DialogField dialog;

    private String icon;

    private BuildText buildText;

    public OperaField(UIComponent owner) {
        this(owner, res.getString(2, "操作"), 3);
        this.setReadonly(true);
    }

    public OperaField(UIComponent owner, String name, int width) {
        super(owner, name, width);
        this.setAlign("center");
        this.setField("_opera_");
        this.setCSSClass_phone("right");
    }

    @Override
    public String getText(Record record) {
        if (getBuildText() != null) {
            HtmlWriter html = new HtmlWriter();
            getBuildText().outputText(record, html);
            return html.toString();
        }
        return this.value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public OperaField setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public OperaField setReadonly(boolean readonly) {
        super.setReadonly(true);
        return this;
    }

    @Override
    public DialogField getDialog() {
        return dialog;
    }

    @Override
    public OperaField setDialog(String dialogfun) {
        this.dialog = new DialogField(dialogfun);
        dialog.setInputId(this.getId());
        return this;
    }

    @Override
    public OperaField setDialog(String dialogfun, String... params) {
        this.dialog = new DialogField(dialogfun);
        dialog.setInputId(this.getId());
        for (String string : params) {
            this.dialog.add(string);
        }
        return this;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public OperaField setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    @Override
    public OperaField createText(BuildText buildText) {
        this.buildText = buildText;
        return this;
    }

    @Override
    public BuildText getBuildText() {
        return buildText;
    }
}
