package cn.cerc.ui.grid;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.ui.core.DataSource;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IField;
import cn.cerc.ui.fields.AbstractField;
import cn.cerc.ui.grid.lines.AbstractGridLine;
import cn.cerc.ui.grid.lines.ChildGridLine;
import cn.cerc.ui.grid.lines.MasterGridLine;
import cn.cerc.ui.parts.UIActionForm;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.core.DataSet;
import cn.cerc.mis.core.IForm;

public abstract class AbstractGrid extends UIComponent implements DataSource {
    // 数据源
    private DataSet dataSet;
    // 支持表格分页
    private MutiPage pages = new MutiPage();
    // 行管理器, 其中第1个一定为masterLine
    private List<AbstractGridLine> lines = new ArrayList<>();
    // 主行
    protected MasterGridLine masterLine;
    // 表单，后不得再使用
    protected UIActionForm form;

    public AbstractGrid(IForm form, UIComponent owner) {
        super(owner);
        this.setId("grid");
        masterLine = new MasterGridLine(this);
        lines.add(masterLine);
        pages.setRequest(form.getRequest());
    }

    @Override
    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
        pages.setDataSet(dataSet);
    }

    @Override
    public void addField(IField field) {
        if (field instanceof AbstractField) {
            AbstractField obj = (AbstractField) field;
            obj.setOwner(masterLine);
        }
        masterLine.addField(field);
    }

    public MutiPage getPages() {
        return pages;
    }

    public List<AbstractField> getFields() {
        List<AbstractField> items = new ArrayList<>();
        for (IField obj : lines.get(0).getFields()) {
            if (obj instanceof AbstractField)
                items.add((AbstractField) obj);
        }
        return items;
    }

    @Deprecated
    public UIActionForm getForm() {
        return form;
    }

    @Deprecated
    public void setForm(UIActionForm form) {
        this.form = form;
    }

    public abstract void outputGrid(HtmlWriter html);

    public abstract UIComponent getExpender();

    public List<AbstractGridLine> getLines() {
        return lines;
    }

    public AbstractGridLine getLine(int index) {
        if (index == lines.size())
            lines.add(new ChildGridLine(this));
        return lines.get(index);
    }

    public MasterGridLine getMasterLine() {
        return masterLine;
    }
}
