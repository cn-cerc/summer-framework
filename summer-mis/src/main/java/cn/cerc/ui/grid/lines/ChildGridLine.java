package cn.cerc.ui.grid.lines;

import cn.cerc.ui.core.DataSource;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IColumn;
import cn.cerc.ui.core.IField;
import cn.cerc.ui.fields.AbstractField;
import cn.cerc.ui.grid.RowCell;

public class ChildGridLine extends AbstractGridLine {

    public ChildGridLine(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void output(HtmlWriter html, int lineNo) {
        html.print("<tr");
        html.print(" id='%s_%s'", "tr" + dataSource.getDataSet().getRecNo(), lineNo);
        if (!this.isVisible())
            html.print(" style=\"display:none\"");
        html.println(">");
        for (RowCell item : this.getCells()) {
            IField objField = item.getFields().get(0);
            html.print("<td");
            if (item.getColSpan() > 1)
                html.print(" colspan=\"%d\"", item.getColSpan());
            if (item.getStyle() != null)
                html.print(" style=\"%s\"", item.getStyle());
            if (item.getAlign() != null)
                html.print(" align=\"%s\"", item.getAlign());
            else if (objField.getAlign() != null)
                html.print(" align=\"%s\"", objField.getAlign());

            if (item.getRole() != null)
                html.print(" role=\"%s\"", item.getRole());
            else if (item.getFields().get(0).getField() != null)
                html.print(" role=\"%s\"", item.getFields().get(0).getField());

            html.print(">");
            for (IField obj : item.getFields()) {
                if (obj instanceof AbstractField) {
                    AbstractField field = (AbstractField) obj;
                    if (field instanceof IColumn)
                        html.print(((IColumn) field).format(dataSource.getDataSet().getCurrent()));
                    else if (field instanceof AbstractField)
                        outputField(html, field);
                    else
                        throw new RuntimeException("暂不支持的数据类型：" + field.getClass().getName());
                    if (field.getTitle() != null && !"".equals(field.getTitle()))
                        html.print("<span style='float: left;'>%s：</span> ", field.getTitle());
                }
            }
            html.println("</td>");
        }
        html.println("</tr>");
    }

    @Override
    public void addField(IField field) {
        getFields().add(field);
        RowCell col;
        col = new RowCell();
        col.setAlign(field.getAlign());
        col.setRole(field.getField());
        getCells().add(col);
        col.addField(field);
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
