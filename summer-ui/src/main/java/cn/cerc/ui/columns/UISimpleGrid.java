package cn.cerc.ui.columns;

import java.util.Map;

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
            Map<String, String> items = this.getDataSet().getFieldDefs().getItems();
            for (String code : items.keySet()) {
                new StringColumn(this).setCode(code).setName(items.get(code));
            }
        }
    }

    public IColumn getColumn(int index) {
        initColumns();
        return this.getColumns().get(index);
    }
}
