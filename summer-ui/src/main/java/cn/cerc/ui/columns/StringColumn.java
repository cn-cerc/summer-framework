package cn.cerc.ui.columns;

import cn.cerc.mis.core.IForm;
import cn.cerc.mis.magic.FieldDefine;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IReadonlyOwner;
import cn.cerc.ui.core.UIOriginComponent;
import cn.cerc.ui.other.BuildText;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UIInput;
import cn.cerc.ui.vcl.UILabel;

public class StringColumn extends AbstractColumn implements IDataColumn {
    private UIInput input = new UIInput(this);
    private UIComponent helper;
    @Deprecated
    private BuildText buildText;
    private boolean readonly;
    private boolean required;

    public StringColumn(UIComponent owner) {
        super(owner);
        if (owner instanceof IReadonlyOwner) {
            this.setReadonly(((IReadonlyOwner) owner).isReadonly());
        }
    }

    public StringColumn(UIComponent owner, FieldDefine field) {
        super(owner);
        this.setCode(field.getCode()).setName(field.getName());
        if (owner instanceof IReadonlyOwner) {
            this.setReadonly(((IReadonlyOwner) owner).isReadonly());
        }
        input.setName(field.getCode());
    }

    public StringColumn(UIComponent owner, String name, String code) {
        super(owner);
        this.setCode(code).setName(name);
        if (owner instanceof IReadonlyOwner) {
            this.setReadonly(((IReadonlyOwner) owner).isReadonly());
        }
        input.setName(code);
    }

    public StringColumn(UIComponent owner, String name, String code, int width) {
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
        String text = getRecord().getString(this.getCode());
        if (this.readonly) {
            if (buildText != null) {
                buildText.outputText(getRecord(), html);
            }
            html.print(text);
        } else {
            input.setValue(text);
            input.output(html);
        }
    }

    private void outputCellPhone(HtmlWriter html) {
        String text = getRecord().getString(this.getCode());
        if (this.readonly) {
            html.print(getName());
            if (!"".equals(getName())) {
                html.print("：");
            }
            html.print(text);
        } else {
            UILabel label = new UILabel();
            label.setFocusTarget(this.getCode());
            if (!"".equals(getName())) {
                label.setText(getName() + "：");
            }
            label.output(html);

            input.setId(getCode());
            input.setReadonly(readonly);
            input.setValue(text);
            input.output(html);
        }
    }

    @Override
    public void outputLine(HtmlWriter html) {
        String text = getRecord().getString(this.getCode());

        if (!this.isHidden()) {
            UILabel label = new UILabel();
            label.setFocusTarget(this.getCode());
            if (!"".equals(getName())) {
                label.setText(getName() + "：");
            }
            label.output(html);
        }

        input.setId(getCode());
        input.setReadonly(readonly);
        input.setValue(text);
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

    @Deprecated
    public StringColumn createText(BuildText buildText) {
        this.buildText = buildText;
        return this;
    }

    @Override
    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public StringColumn setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    public StringColumn setPlaceholder(String placeholder) {
        input.setPlaceholder(placeholder);
        return this;
    }

    public String getPlaceholder() {
        return input.getPlaceholder();
    }

    @Override
    public boolean isHidden() {
        return input.isHidden();
    }

    @Override
    public StringColumn setHidden(boolean hidden) {
        input.setHidden(hidden);
        return this;
    }

    public StringColumn setInputType(String inputType) {
        input.setInputType(inputType);
        return this;
    }

    public StringColumn setRequired(boolean required) {
        input.setRequired(required);
        return this;
    }

    public boolean isRequired() {
        return required;
    }

}
