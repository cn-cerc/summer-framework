package cn.cerc.ui.columns;

import java.util.Map;

import cn.cerc.mis.core.IForm;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IReadonlyOwner;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UILabel;
import cn.cerc.ui.vcl.UISelect;

public class OptionColumn extends AbstractColumn implements IDataColumn {
    private UISelect select;
    private boolean readonly;
    private boolean important;
    private boolean hidden;

    public OptionColumn(UIComponent owner) {
        super(owner);
        this.select = new UISelect();
        if (owner instanceof IReadonlyOwner) {
            this.setReadonly(((IReadonlyOwner) owner).isReadonly());
        }
        this.setReadonly(this.isReadonly());
    }

    public OptionColumn(UIComponent owner, String name, String code) {
        super(owner);
        this.setCode(code).setName(name);
        this.select = new UISelect();
        if (owner instanceof IReadonlyOwner) {
            this.setReadonly(((IReadonlyOwner) owner).isReadonly());
        }
        this.setReadonly(this.isReadonly());
    }

    public OptionColumn(UIComponent owner, String name, String code, int width) {
        super(owner);
        this.setCode(code).setName(name).setSpaceWidth(width);
        this.select = new UISelect();
        if (owner instanceof IReadonlyOwner) {
            this.setReadonly(((IReadonlyOwner) owner).isReadonly());
        }
        this.setReadonly(this.isReadonly());
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
        String key = getRecord().getString(getCode());
        html.print(getName() + "：");
        if (this.readonly) {
            html.print(select.getOptions().getOrDefault(key, key));
        } else {
            select.setName(getCode()).setSize(getSize()).setSelected(key);
            select.output(html);

            if (this.isImportant()) {
                html.print("<font>*</font>");
            }

            html.print("<span></span>");
        }
    }

    private void outputCellWeb(HtmlWriter html) {
        String current = getRecord().getString(getCode());

        select.setName(this.getCode()).setSize(getSize()).setReadonly(isReadonly()).setSelected(current);
        select.output(html);

        if (this.important) {
            html.print("<font>*</font>");
        }

        html.print("<span></span>");
    }

    @Override
    public void outputLine(HtmlWriter html) {
        String text = getRecord().getString(getCode());
        UILabel label = new UILabel();
        label.setText(getName() + "：");
        label.output(html);

        select.setName(getCode()).setSize(this.getSize()).setSelected(text);
        select.output(html);

        html.print("<span></span>");
    }

    public OptionColumn setOptions(Map<String, String> items) {
        select.setOptions(items);
        return this;
    }

    public Map<String, String> getOptions() {
        return select.getOptions();
    }

    @Override
    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public OptionColumn setReadonly(boolean readonly) {
        this.readonly = readonly;
        if (this.select != null) {
            this.select.setReadonly(readonly);
        }
        return this;
    }

    public int getSize() {
        return select.getSize();
    }

    public OptionColumn setSize(int size) {
        select.setSize(size);
        return this;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public OptionColumn setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public void put(String key, String value) {
        select.getOptions().put(key, value);
    }

    public void setOptions(Enum<?>[] values) {
        setOptions(values, false);
    }

    public void setOptions(Enum<?>[] values, boolean allFiler) {
        if (allFiler) {
            select.getOptions().put("", "全部");
        }
        for (Enum<?> item : values) {
            select.getOptions().put(String.valueOf(item.ordinal()), item.name());
        }

    }

}
