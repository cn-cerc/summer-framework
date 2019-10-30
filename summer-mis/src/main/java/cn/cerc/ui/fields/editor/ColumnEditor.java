package cn.cerc.ui.fields.editor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IColumn;
import cn.cerc.ui.core.IField;
import cn.cerc.ui.fields.AbstractField;
import cn.cerc.ui.grid.DataGrid;
import cn.cerc.ui.grid.lines.AbstractGridLine;
import cn.cerc.ui.grid.lines.MasterGridLine;

public class ColumnEditor {
    private AbstractField owner;
    private boolean init = false;
    private DataSet dataSet;
    private List<IField> columns;
    private String onUpdate;
    private List<String> dataField = new ArrayList<>(); // 设置的字段列表
    private AbstractGridLine gridLine;

    public ColumnEditor(AbstractField owner) {
        this.owner = owner;
        if (!(owner.getOwner() instanceof AbstractGridLine))
            throw new RuntimeException("不支持的数据类型：" + owner.getOwner().getClass().getName());
        gridLine = (AbstractGridLine) (owner.getOwner());
    }

    public String getOnUpdate() {
        return onUpdate;
    }

    public void setOnUpdate(String onUpdate) {
        this.onUpdate = onUpdate;
    }

    public String format(Record ds) {
        String data = ds.getString(owner.getField());
        if (owner.getBuildText() != null) {
            HtmlWriter html = new HtmlWriter();
            owner.getBuildText().outputText(ds, html);
            data = html.toString();
        } else if (ds.getField(owner.getField()) instanceof Double) {
            DecimalFormat df = new DecimalFormat("0.####");
            data = df.format(ds.getDouble(owner.getField()));
        }

        if (!this.init) {
            dataSet = gridLine.getDataSet();
            columns = new ArrayList<>();
            for (IField field : gridLine.getFields()) {
                if (field instanceof IColumn) {
                    if (((AbstractField) field).isReadonly())
                        continue;
                    if (field.getWidth() == 0)
                        continue;
                    columns.add(field);
                }
            }
            if (gridLine.getOwner() instanceof DataGrid) {
                DataGrid grid = (DataGrid) gridLine.getOwner();
                if (columns.size() > 0 && grid.getPrimaryKey() == null)
                    throw new RuntimeException("BaseGrid.primaryKey is null");
            }
            this.init = true;
        }
        HtmlWriter html = new HtmlWriter();
        String inputStyle = "";
        html.print("<input");
        if (gridLine instanceof MasterGridLine)
            html.print(" id='%s'", this.getDataId());
        else {
            if (owner.getId() != null)
                html.print(" id='%s'", owner.getId());
            inputStyle = "width:80%;";
        }
        inputStyle += "border: 1px solid #dcdcdc;";
        html.print(" type='text'");
        html.print(" name='%s'", owner.getField());
        html.print(" value='%s'", data);
        html.print(" autocomplete='off'");
        html.print(" data-%s='[%s]'", owner.getField(), data);
        if (gridLine instanceof MasterGridLine) {
            html.print(" data-focus='[%s]'", this.getDataFocus());
            if (owner.getAlign() != null)
                inputStyle += String.format("text-align:%s;", owner.getAlign());
            if (owner.getOnclick() != null) {
                html.print(" onclick=\"%s\"", owner.getOnclick());
            } else
                html.print(" onclick='this.select()'");
        }
        if (!"".equals(inputStyle))
            html.print(" style='%s'", inputStyle);
        html.print(" onkeydown='return tableDirection(event,this)'");
        if (dataField.size() > 0) {
            for (String field : dataField) {
                html.print(" data-%s='%s'", field, ds.getString(field));
            }
        }
        if (onUpdate != null)
            html.print(" oninput=\"tableOnChanged(this,'%s')\"", onUpdate);
        else
            html.print(" oninput='tableOnChanged(this)'");
        html.println("/>");
        return html.toString();
    }

    private String getDataId() {
        int recNo = dataSet.getRecNo();
        int colNo = columns.indexOf(owner);
        String selfId = String.format("%d_%d", recNo, colNo);
        return selfId;
    }

    private String getDataFocus() {
        int recNo = dataSet.getRecNo();
        int colNo = columns.indexOf(owner);

        String prior = recNo > 1 ? String.format("%d_%d", recNo - 1, colNo) : "0";
        String next = recNo < dataSet.size() ? String.format("%d_%d", recNo + 1, colNo) : "0";
        String left = colNo > 0 ? String.format("%d_%d", recNo, colNo - 1) : "0";
        String right = colNo < columns.size() - 1 ? String.format("%d_%d", recNo, colNo + 1) : "0";
        return String.format("\"%s\",\"%s\",\"%s\",\"%s\"", left, prior, right, next);
    }

    public AbstractGridLine getGridLine() {
        return gridLine;
    }

    public void setGridLine(AbstractGridLine gridLine) {
        this.gridLine = gridLine;
    }

    /**
     * 给元素设置data-*属性
     * 
     * @return 要设置的字段列表
     */
    public List<String> getDataField() {
        return dataField;
    }
}
