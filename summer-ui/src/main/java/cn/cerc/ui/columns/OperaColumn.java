package cn.cerc.ui.columns;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UILabel;

public class OperaColumn extends AbstractColumn implements IDataColumn {
    private String text;
    private boolean readonly;
    private boolean hidden;

    public OperaColumn(UIComponent owner) {
        super(owner);
        this.setSpaceWidth(4);
        this.setName("操作");
    }

    @Override
    public void outputCell(HtmlWriter html) {
        UILabel label = new UILabel();
        label.setText(text);
        label.output(html);
    }

    @Override
    public void outputLine(HtmlWriter html) {
        html.print("Not support oprea mode");
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public OperaColumn setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public OperaColumn setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

}
