package cn.cerc.ui.columns;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.core.ClassResource;
import cn.cerc.core.DataSet;
import cn.cerc.core.Utils;
import cn.cerc.mis.core.IForm;
import cn.cerc.ui.SummerUI;
import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IOriginOwner;
import cn.cerc.ui.core.IReadonlyOwner;
import cn.cerc.ui.core.UICustomComponent;
import cn.cerc.ui.core.UIOriginComponent;
import cn.cerc.ui.grid.DataGrid;
import cn.cerc.ui.grid.MutiPage;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.table.UITd;
import cn.cerc.ui.vcl.table.UITr;

public class UIGrid extends UIOriginComponent implements IReadonlyOwner {
    private static final ClassResource res = new ClassResource(DataGrid.class, SummerUI.ID);
    private final double MaxWidth = 600;
    // 专用于电脑
    private List<UIGridLine> gridLines = new ArrayList<>();
    // 专用于手机
    private List<UIPhoneLine> phoneLines = new ArrayList<>();
    // 数据源
    private DataSet dataSet;
    // 支持表格分页
    private MutiPage pages = new MutiPage();
    // 所有字段对象
    private List<IColumn> columns = new ArrayList<>();
    // 默认表格内容为只读
    private boolean readonly = true;
    //
    private IForm form;
    private IDefineEmptyData defineEmptyData;

    public UIGrid(UIComponent owner) {
        super(owner);
        this.setId("grid");
        this.setCssClass("grid");
        if (owner instanceof IOriginOwner) {
            form = (IForm) ((IOriginOwner) owner).getOrigin();
            pages.setRequest(form.getRequest());
        }
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
        pages.setDataSet(dataSet);
    }

    public MutiPage getPages() {
        return pages;
    }

    public UIGridLine getMasterLine() {
        if (gridLines.size() == 0)
            gridLines.add(new UIGridLine(this));

        return gridLines.get(0);
    }

    public UIGridLine getExpenderLine() {
        if (gridLines.size() < 2)
            gridLines.add(new UIGridLine(this));

        return gridLines.get(1);
    }

    @Override
    public void output(HtmlWriter html) {
        // 检查是否在手机模式下
        if (this.getOrigin() instanceof IForm) {
            IForm form = (IForm) this.getOrigin();
            if (form.getClient().isPhone()) {
                outputPhone(html);
                return;
            }
        }

        outputPc(html);
    }

    private void outputPhone(HtmlWriter html) {
        html.print("<div class='%s'>", this.cssClass);
        DataSet dataSet = this.getDataSet();
        dataSet.first();
        while (dataSet.fetch()) {
            html.print("<div class=\"record\" data-record=\"%d\">", dataSet.getRecNo());
            html.print("<ul>");
            for (UIPhoneLine block : this.phoneLines) {
                block.setRecord(dataSet.getCurrent());
                html.print("<li");
                if (block instanceof UICustomComponent) {
                    UICustomComponent item = block;
                    if (item.getCssClass() != null) {
                        html.print(" class=\"%s\"", item.getCssClass());
                    }
                }
                html.print(">");
                block.output(html);
                html.println("</li>");
            }
            html.println("</ul>");
            html.println("</div>");
        }
        html.print("</div>");
    }

    private void outputPc(HtmlWriter html) {
        // 默认输出普通表格
        html.print("<div class='%s'>", this.cssClass);
        DataSet dataSet = this.getDataSet();
        MutiPage pages = this.getPages();

        double sumFieldWidth = 0;
        for (UIComponent item : this.getMasterLine()) {
            if (item instanceof IColumn) {
                sumFieldWidth += ((IColumn) item).getSpaceWidth();
            }
        }

        if (sumFieldWidth < 0) {
            throw new RuntimeException(res.getString(1, "总列宽不允许小于1"));
        }
        if (sumFieldWidth > MaxWidth) {
            throw new RuntimeException(String.format(res.getString(2, "总列宽不允许大于%s"), MaxWidth));
        }

        html.println("<table>");

        // 输出表头
        html.println("<tr>");
        for (UIComponent item : this.getMasterLine()) {
            if (!(item instanceof IColumn)) {
                continue;
            }
            IColumn column = (IColumn) item;
            html.print("<th");
            if (column.getSpaceWidth() == 0) {
                html.print(" style=\"display:none\"");
            } else {
                double val = Utils.roundTo(column.getSpaceWidth() / sumFieldWidth * 100, -2);
                html.print(" width=\"%f%%\"", val);
            }

            html.print("onclick=\"gridSort(this,'%s')\"", column.getCode());
            html.print(">");
            html.print(column.getName());
            html.println("</th>");
        }
        html.println("</tr>");

        // 输出表身
        if (dataSet.size() > 0) {
            int i = pages.getBegin();
            while (i <= pages.getEnd()) {
                dataSet.setRecNo(i + 1);
                html.println("<tr>");
                for (UIComponent item : this.getMasterLine()) {
                    html.print("<td>");
                    if ((item instanceof IColumn)) {
                        IColumn column = (IColumn) item;
                        if (item instanceof IDataColumn) {
                            ((IDataColumn) column).setRecord(dataSet.getCurrent());
                        }
                        column.outputCell(html);
                    } else {
                        item.output(html);
                    }
                    html.print("</td>");
                }
                html.println("</tr>");
                // 下一行
                i++;
            }
        } else {
            UITr tr = new UITr().setCssClass("empty");
            UITd td = new UITd(tr).setColspan(this.getMasterLine().getComponents().size());
            if (defineEmptyData != null) {
                defineEmptyData.execute(this, td);
            } else {
                td.setText("（没有数据）");
            }
            tr.output(html);
        }
        html.println("</table>");
        html.print("</div>");
    }

    public interface IDefineEmptyData {
        void execute(UIGrid sender, UITd td);
    }

    public UIGrid defineEmptyData(IDefineEmptyData defineEmptyData) {
        this.defineEmptyData = defineEmptyData;
        return this;
    }

    @Override
    public String getCssClass() {
        return cssClass;
    }

    @Override
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public UIPhoneLine addPhoneLine(int... percents) {
        return addPhoneLine(getMasterLine(), percents);
    }

    public UIPhoneLine addPhoneLine(UIGridLine gridLine, int... percents) {
        UIPhoneLine block = new UIPhoneLine(this);
        block.setAttachLine(getMasterLine());
        block.buildCells(percents);
        phoneLines.add(block);
        return block;
    }

    @Override
    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public UIGrid setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    @Override
    public void addComponent(Component component) {
        if (component instanceof IColumn) {
            IColumn column = (IColumn) component;
            if (component instanceof IDataColumn) {
                ((IDataColumn) column).setReadonly(readonly);
            }
            getMasterLine().addComponent(component);
            columns.add(column);
        } else {
            super.addComponent(component);
        }
    }

    public List<IColumn> getColumns() {
        return columns;
    }
}
