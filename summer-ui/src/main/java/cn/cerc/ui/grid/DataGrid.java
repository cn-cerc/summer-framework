package cn.cerc.ui.grid;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ClassResource;
import cn.cerc.core.DataSet;
import cn.cerc.core.Utils;
import cn.cerc.mis.core.IForm;
import cn.cerc.ui.SummerUI;
import cn.cerc.ui.core.DataSource;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IField;
import cn.cerc.ui.fields.AbstractField;
import cn.cerc.ui.grid.lines.AbstractGridLine;
import cn.cerc.ui.grid.lines.ChildGridLine;
import cn.cerc.ui.grid.lines.ExpenderGridLine;
import cn.cerc.ui.grid.lines.MasterGridLine;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UIForm;

public class DataGrid extends UIComponent implements DataSource {
    private static final Logger log = LoggerFactory.getLogger(DataGrid.class);
    private static final ClassResource res = new ClassResource(DataGrid.class, SummerUI.ID);
    private static final double MaxWidth = 600;
    // 行管理器, 其中第1个一定为masterLine
    private List<AbstractGridLine> lines = new ArrayList<>();
    // 扩展行
    private ExpenderGridLine expender;
    //手机行
    protected List<PhoneLine> phoneLines = new ArrayList<>();
    // 表单，后不得再使用
    private UIForm uiform;
    // 数据源
    private DataSet dataSet;
    // 支持表格分页
    private MutiPage pages = new MutiPage();
    private IForm form;
    // 以下参数为WEB模式下时专用
    private String CssClass = "dbgrid";
    private String CssStyle;
    // 输出每列时的事件
    private OutputEvent beforeOutput;

    public DataGrid(IForm form, UIComponent owner) {
        super(owner);
        this.setId("grid");
        this.form = form;
        lines.add(new MasterGridLine(this));
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
            obj.setOwner(getMasterLine());
        }
        getMasterLine().addField(field);
    }

    public MutiPage getPages() {
        return pages;
    }

    public List<AbstractField> getFields() {
        List<AbstractField> items = new ArrayList<>();
        for (IField obj : lines.get(0).getFields()) {
            if (obj instanceof AbstractField) {
                items.add((AbstractField) obj);
            }
        }
        return items;
    }

    @Deprecated
    public UIForm getForm() {
        return uiform;
    }

    @Deprecated
    public void setForm(UIForm form) {
        this.uiform = form;
    }

    public final UIComponent getExpender() {
        if (form.getClient().isPhone()) {
            return this;
        } else {
            if (expender == null) {
                expender = new ExpenderGridLine(this);
                this.getLines().add(expender);
            }
            return expender;
        }
    }

    public List<AbstractGridLine> getLines() {
        return lines;
    }

    public AbstractGridLine getLine(int index) {
        if (index == lines.size()) {
            lines.add(new ChildGridLine(this));
        }
        return lines.get(index);
    }

    public final MasterGridLine getMasterLine() {
        return (MasterGridLine) lines.get(0);
    }

    @Override
    public final boolean isReadonly() {
        return true;
    }

    @Override
    public final void updateValue(String id, String code) {

    }

    public final String getCSSClass() {
        return CssClass;
    }

    public final void setCSSClass(String CSSClass) {
        this.CssClass = CSSClass;
    }

    @Override
    public final void output(HtmlWriter html) {
        html.print("<div class='scrollArea'>");
        if (form.getClient().isPhone()) {
            if (this.getDataSet().size() > 0) {
                outputGrid(html);
            }
        } else {
            outputGrid(html);
        }
        html.print("</div>");
    }

    private void outputGrid(HtmlWriter html) {
        if (getForm() != null)
            getForm().outHead(html);
        if (form.getClient().isPhone())
            this.outputPhoneGrid(html);
        else
            this.outputWebGrid(html);
        if (getForm() != null)
            getForm().outFoot(html);
    }

    private void outputWebGrid(HtmlWriter html) {
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

        html.print("<table class=\"%s\"", this.CssClass);
        if (this.CssStyle != null) {
            html.print(" style=\"%s\"", this.CssStyle);
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

    private void outputPhoneGrid(HtmlWriter html) {
        DataSet dataSet = this.getDataSet();
        MutiPage pages = this.getPages();
        if (dataSet.size() == 0) {
            return;
        }

        html.println(String.format("<ol class=\"%s\">", "context"));

        int i = pages.getBegin();
        while (i <= pages.getEnd()) {
            dataSet.setRecNo(i + 1);
            int flag = 0;
            html.println("<li>");
            for (PhoneLine line : this.phoneLines) {
                if (line.isTable()) {
                    if (flag == 0) {
                        html.println("<table>");
                        flag = 1;
                    } else if (flag == 2) {
                        html.println("</table>");
                        html.println("<table>");
                    }
                } else {
                    if (flag == 1) {
                        html.println("</table>");
                        flag = 2;
                    }
                }
                line.output(html);
            }
            if (flag == 1) {
                html.println("</table>");
            }
            html.println("</li>");
            i++;
        }
        html.println("</ol>");
        return;
    }

    public String getCSSStyle() {
        return CssStyle;
    }

    public void setCSSStyle(String cSSStyle) {
        if (form.getClient().isPhone())
            log.warn("only support web device");
        CssStyle = cSSStyle;
    }

    public OutputEvent getBeforeOutput() {
        if (form.getClient().isPhone())
            log.warn("only support web device");
        return beforeOutput;
    }

    public void setBeforeOutput(OutputEvent beforeOutput) {
        if (form.getClient().isPhone())
            log.warn("only support web device");
        this.beforeOutput = beforeOutput;
    }

    public String getPrimaryKey() {
        if (form.getClient().isPhone())
            return null;
        else
            return getMasterLine().getPrimaryKey();
    }

    public DataGrid setPrimaryKey(String primaryKey) {
        if (form.getClient().isPhone()) {
            log.debug("only support web device");
        } else {
            this.getMasterLine().setPrimaryKey(primaryKey);
        }
        return this;
    }

    public PhoneLine addLine() {
        if (!form.getClient().isPhone())
            log.warn("only support phone device");
        PhoneLine line = new PhoneLine(this);
        phoneLines.add(line);
        return line;
    }

}
