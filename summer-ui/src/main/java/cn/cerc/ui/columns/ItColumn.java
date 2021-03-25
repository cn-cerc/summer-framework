package cn.cerc.ui.columns;

import cn.cerc.core.DataSet;
import cn.cerc.mis.core.IForm;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class ItColumn extends AbstractColumn implements IDataColumn {

    private boolean readonly;
    private boolean hidden;

    public ItColumn(UIComponent owner) {
        super(owner);
        this.setName("序");
    }

    @Override
    public void outputCell(HtmlWriter html) {
        DataSet dataSet = this.getRecord().getDataSet();
        if (dataSet == null) {
            html.print("dataSet is null");
            return;
        }
        html.print(String.valueOf(dataSet.getRecNo()));

        if (this.getOrigin() instanceof IForm) {
            IForm form = (IForm) this.getOrigin();
            if (form.getClient().isPhone()) {
                html.print("#");
            }
        }
    }

    @Override
    public void outputLine(HtmlWriter html) {

    }

    @Override
    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public ItColumn setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public ItColumn setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }
    
}
