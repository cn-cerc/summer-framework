package cn.cerc.ui.columns;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class UISimpleGrid extends UIGrid {

    public UISimpleGrid(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        initColumns();
        super.output(html);
    }

    public void initColumns() {
        if (this.getColumns().size() == 0) {
            for (String code : this.getDataSet().getFieldDefs()) {
                new StringColumn(this).setCode(code).setName(code);
            }
        }
    }

    public IColumn getColumn(int index) {
        initColumns();
        return this.getColumns().get(index);
    }
}
