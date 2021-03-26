package cn.cerc.ui.fields;

import cn.cerc.core.ClassResource;
import cn.cerc.core.Record;
import cn.cerc.ui.SummerUI;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class OperaField extends AbstractField {
    private static final ClassResource res = new ClassResource(OperaField.class, SummerUI.ID);

    private String value = res.getString(1, "内容");

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
        if (buildText != null) {
            HtmlWriter html = new HtmlWriter();
            buildText.outputText(record, html);
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
}
