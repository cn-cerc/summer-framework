package cn.cerc.ui.columns;

import cn.cerc.mis.core.IForm;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IReadonlyOwner;
import cn.cerc.ui.core.UIOriginComponent;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UIInput;
import cn.cerc.ui.vcl.UILabel;

public class BooleanColumn extends AbstractColumn implements IDataColumn {

    private UIInput input = new UIInput(this);
    private UIComponent helper;
    private Object origin;
    private boolean readonly;

    public BooleanColumn(UIComponent owner) {
        super(owner);
        if (owner instanceof IReadonlyOwner) {
            this.setReadonly(((IReadonlyOwner) owner).isReadonly());
        }
    }

    public BooleanColumn(UIComponent owner, String name, String code) {
        super(owner);
        this.setCode(code).setName(name).setSpaceWidth(10);
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

    private void outputCellPhone(HtmlWriter html) {
        boolean value = this.getRecord().getBoolean(getCode());
        if (this.readonly) {
            html.print(getName() + "：");
            html.print(value ? "是" : "否");
        } else {
            UILabel label = new UILabel();
            label.setFocusTarget(this.getCode());
            label.setText(getName() + "：");
            label.output(html);

            input.setInputType(UIInput.TYPE_CHECKBOX);
            input.setChecked(value);
            input.setId(getCode());
            input.output(html);
        }
    }

    private void outputCellWeb(HtmlWriter html) {
        String text = getRecord().getString(this.getCode());
        if (this.readonly) {
            html.print(text);
        } else {
            input.setValue(text);
            input.output(html);
        }
    }

    @Override
    public void outputLine(HtmlWriter html) {
        boolean value = this.getRecord().getBoolean(getCode());

        if (!this.isHidden()) {
            UILabel label = new UILabel();
            label.setFocusTarget(this.getCode());
            label.setText(getName() + "：");
            label.output(html);
        }

        input.setId(getCode());
        input.setInputType(UIInput.TYPE_CHECKBOX);
        input.setChecked(value);
        input.setReadonly(readonly);
        input.output(html);

        if (!this.isHidden()) {
            html.print("<span>");
            if (this.helper != null) {
                helper.output(html);
            }
            html.println("</span>");
        }
    }

    public UIComponent getHelper() {
        if (helper != null)
            helper = new UIOriginComponent(this);
        return helper;
    }

    @Override
    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public BooleanColumn setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    @Override
    public void setOrigin(Object origin) {
        this.origin = origin;
    }

    @Override
    public Object getOrigin() {
        return origin;
    }

    @Override
    public boolean isHidden() {
        return input.isHidden();
    }

    @Override
    public BooleanColumn setHidden(boolean hidden) {
        input.setHidden(hidden);
        return this;
    }

}
