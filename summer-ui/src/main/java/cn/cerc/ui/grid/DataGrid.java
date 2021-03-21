package cn.cerc.ui.grid;

import cn.cerc.core.ClassResource;
import cn.cerc.core.DataSet;
import cn.cerc.core.Utils;
import cn.cerc.mis.core.IForm;
import cn.cerc.ui.SummerUI;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IField;
import cn.cerc.ui.grid.lines.AbstractGridLine;
import cn.cerc.ui.grid.lines.ChildGridLine;
import cn.cerc.ui.grid.lines.ExpenderGridLine;
import cn.cerc.ui.parts.UIComponent;

public class DataGrid extends AbstractGrid {
    private static final ClassResource res = new ClassResource(DataGrid.class, SummerUI.ID);

    private final double MaxWidth = 600;
    // 当前样式选择
    private String CSSClass = "dbgrid";
    private String CSSStyle;
    // 扩展对象
    private AbstractGridLine expender;
    // 输出每列时的事件
    private OutputEvent beforeOutput;

    public DataGrid(IForm form, UIComponent owner) {
        super(form, owner);
    }

    @Override
    public void output(HtmlWriter html) {
        html.print("<div class='scrollArea'>");
        if (getForm() != null) {
            getForm().outHead(html);
            outputGrid(html);
            getForm().outFoot(html);
        } else {
            outputGrid(html);
        }
        html.print("</div>");
    }

    @Override
    public void outputGrid(HtmlWriter html) {
        DataSet dataSet = this.getDataSet();
        MutiPage pages = this.getPages();

        double sumFieldWidth = 0;
        for (RowCell cell : this.getMasterLine().getOutputCells()) {
            sumFieldWidth += cell.getFields().get(0).getWidth();
        }

        if (sumFieldWidth < 0) {
            throw new RuntimeException(res.getString(1, "总列宽不允许小于1"));
        }
        if (sumFieldWidth > MaxWidth) {
            throw new RuntimeException(String.format(res.getString(2, "总列宽不允许大于%s"), MaxWidth));
        }

        html.print("<table class=\"%s\"", this.getCSSClass());
        if (this.getCSSStyle() != null) {
            html.print(" style=\"%s\"", this.getCSSStyle());
        }
        html.println(">");

        html.println("<tr>");
        for (RowCell cell : this.getMasterLine().getOutputCells()) {
            IField field = cell.getFields().get(0);
            html.print("<th");
            if (field.getWidth() == 0) {
                html.print(" style=\"display:none\"");
            } else {
                double val = Utils.roundTo(field.getWidth() / sumFieldWidth * 100, -2);
                html.print(" width=\"%f%%\"", val);
            }

            html.print("onclick=\"gridSort(this,'%s')\"", field.getField());
            html.print(">");
            html.print(field.getTitle());
            html.println("</th>");
        }
        html.println("</tr>");
        if (dataSet.size() > 0) {
            int i = pages.getBegin();
            while (i <= pages.getEnd()) {
                dataSet.setRecNo(i + 1);
                for (int lineNo = 0; lineNo < this.getLines().size(); lineNo++) {
                    AbstractGridLine line = this.getLine(lineNo);
                    if (line instanceof ExpenderGridLine) {
                        line.getCell(0).setColSpan(this.getMasterLine().getFields().size());
                    }
                    if (line instanceof ChildGridLine && this.beforeOutput != null) {
                        beforeOutput.process(line);
                    }
                    line.output(html, lineNo);
                }
                // 下一行
                i++;
            }
        }
        html.println("</table>");
    }

    @Override
    public UIComponent getExpender() {
        if (expender == null) {
            expender = new ExpenderGridLine(this);
            this.getLines().add(expender);
        }
        return expender;
    }

    public String getCSSClass() {
        return CSSClass;
    }

    public void setCSSClass(String CSSClass) {
        this.CSSClass = CSSClass;
    }

    public String getCSSStyle() {
        return CSSStyle;
    }

    public void setCSSStyle(String cSSStyle) {
        CSSStyle = cSSStyle;
    }

    public String getPrimaryKey() {
        return masterLine.getPrimaryKey();
    }

    public DataGrid setPrimaryKey(String primaryKey) {
        this.masterLine.setPrimaryKey(primaryKey);
        return this;
    }
//
//    @Override
//    public boolean isReadonly() {
//        return true;
//    }

    @Override
    public void updateValue(String id, String code) {

    }

    public OutputEvent getBeforeOutput() {
        return beforeOutput;
    }

    public void setBeforeOutput(OutputEvent beforeOutput) {
        this.beforeOutput = beforeOutput;
    }

}
