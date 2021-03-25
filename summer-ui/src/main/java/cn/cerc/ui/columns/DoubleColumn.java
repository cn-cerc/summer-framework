package cn.cerc.ui.columns;

import java.text.DecimalFormat;

import cn.cerc.mis.core.IForm;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IReadonlyOwner;
import cn.cerc.ui.core.UIOriginComponent;
import cn.cerc.ui.other.BuildText;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UIInput;
import cn.cerc.ui.vcl.UILabel;

public class DoubleColumn extends AbstractColumn implements IDataColumn {
    private UIInput input = new UIInput(this);
    private UIComponent helper;
    private BuildText buildText;
    private boolean readonly;
    private String format = "0.####";

    public DoubleColumn(UIComponent owner) {
        super(owner);
        if (owner instanceof IReadonlyOwner) {
            this.setReadonly(((IReadonlyOwner) owner).isReadonly());
        }
    }

    public DoubleColumn(UIComponent owner, String name, String code, int width) {
        super(owner);
        this.setCode(code).setName(name).setSpaceWidth(width);
        if (owner instanceof IReadonlyOwner) {
            this.setReadonly(((IReadonlyOwner) owner).isReadonly());
        }
        input.setName(code);
    }

    @Override
    public void outputCell(HtmlWriter html) {
        if (this.getOrigin() instanceof IForm) {
            IForm form = (IForm) this.getOrigin();
            if (form.getClient().isPhone()) {
                outputCellPhone(html);
                return;
            }
        }
        outputCellWeb(html);
    }

    private void outputCellWeb(HtmlWriter html) {
        if (this.readonly) {
            html.print(getText());
        } else {
            input.setValue(getText());
            input.output(html);
        }
    }

    private void outputCellPhone(HtmlWriter html) {
        if (this.readonly) {
            html.print(getName() + "：");
            html.print(getText());
        } else {
            UILabel label = new UILabel();
            label.setFocusTarget(this.getCode());
            label.setText(getName() + "：");
            label.output(html);
            input.setId(getCode());
            input.setReadonly(readonly);
            input.setValue(getText());
            input.output(html);
        }
    }

    private String getText() {
        double value = getRecord().getDouble(this.getCode());
        String text = "0";
        DecimalFormat df = new DecimalFormat(format);
        text = df.format(value);
        return text;
    }

    @Override
    public void outputLine(HtmlWriter html) {
        if (!this.isHidden()) {
            UILabel label = new UILabel();
            label.setFocusTarget(this.getCode());
            label.setText(getName() + "：");
            label.output(html);
        }

        input.setId(getCode());
        input.setReadonly(readonly);
        input.setValue(getText());
        input.output(html);

        if (!this.isHidden()) {
            html.print("<span>");
            if (this.helper != null)
                helper.output(html);
            html.println("</span>");
        }

    }

    public UIComponent getHelper() {
        if (helper != null)
            helper = new UIOriginComponent(this);
        return helper;
    }

    public DoubleColumn createText(BuildText buildText) {
        this.buildText = buildText;
        return this;
    }

    public BuildText getBuildText() {
        return buildText;
    }

    @Override
    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public DoubleColumn setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    public String getFormat() {
        return format;
    }

    public DoubleColumn setFormat(String format) {
        this.format = format;
        return this;
    }

    @Override
    public boolean isHidden() {
        return input.isHidden();
    }

    @Override
    public DoubleColumn setHidden(boolean hidden) {
        input.setHidden(hidden);
        return this;
    }

}
