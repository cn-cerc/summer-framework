package cn.cerc.ui.grid.lines;

import cn.cerc.core.ClassResource;
import cn.cerc.core.DataSet;
import cn.cerc.ui.SummerUI;
import cn.cerc.ui.columns.IColumn;
import cn.cerc.ui.core.DataSource;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IField;
import cn.cerc.ui.core.IFormatColumn;
import cn.cerc.ui.fields.AbstractField;
import cn.cerc.ui.grid.RowCell;

public class ExpenderGridLine extends AbstractGridLine {
    private static final ClassResource res = new ClassResource(ExpenderGridLine.class, SummerUI.ID);

    public ExpenderGridLine(DataSource dataSource) {
        super(dataSource);
        this.setVisible(false);
    }

    @Override
    public void addField(IField field) {
        getFields().add(field);

        RowCell col;
        if (getCells().size() == 0) {
            col = new RowCell();
            getCells().add(col);
        } else {
            col = getCells().get(0);
        }
        col.addField(field);
    }

    @Override
    public void output(HtmlWriter html, int lineNo) {
        DataSet dataSet = dataSource.getDataSet();
        html.print("<tr");
        html.print(" id='%s_%s'", "tr" + dataSet.getRecNo(), lineNo);
        html.print(" role=\"%s\"", dataSet.getRecNo());
        if (!this.isVisible()) {
            html.print(" style=\"display:none\"");
        }
        html.println(">");
        for (RowCell item : this.getCells()) {
            IField objField = item.getFields().get(0);
            html.print("<td");
            if (item.getColSpan() > 1) {
                html.print(" colspan=\"%d\"", item.getColSpan());
            }
            if (item.getStyle() != null) {
                html.print(" style=\"%s\"", item.getStyle());
            }
            if (item.getAlign() != null) {
                html.print(" align=\"%s\"", item.getAlign());
            } else if (objField.getAlign() != null) {
                html.print(" align=\"%s\"", objField.getAlign());
            }
            if (item.getRole() != null) {
                html.print(" role=\"%s\"", item.getRole());
            }

            html.print(">");
            for (IField obj : item.getFields()) {
                if (obj instanceof AbstractField) {
                    AbstractField field = (AbstractField) obj;
                    html.print("<span>");
                    if (!"".equals(field.getName())) {
                        html.print(field.getName());
                        html.print(": ");
                    }
                    if (field instanceof IFormatColumn) {
                        html.print(((IFormatColumn) field).format(dataSource.getDataSet().getCurrent()));
                    } else if (field instanceof AbstractField) {
                        outputField(html, field);
                    } else {
                        throw new RuntimeException(String.format(res.getString(1, "暂不支持的数据类型：%s"), field.getClass().getName()));
                    }
                    html.println("</span>");
                }
            }
            html.println("</td>");
        }
        html.println("</tr>");
    }

    @Override
    public boolean isReadonly() {
        return dataSource.isReadonly();
    }

    @Override
    public void updateValue(String id, String code) {
        dataSource.updateValue(id, code);
    }
}
