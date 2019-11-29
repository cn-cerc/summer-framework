package cn.cerc.mis.excel.output;

import java.util.List;

import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import cn.cerc.core.Utils;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * 批次导出单据明细
 */
public class BatchFormTemplate extends FormTemplate {
    List<DataSet> items;

    @Override
    public void output(WritableSheet sheet) throws RowsExceededException, WriteException {
        int newRow = 0;
        for (DataSet dataSet : items) {
            this.setDataSet(dataSet);
            this.setFooter((template, sheet1) -> {
                Record footer = new Record();
                for (Record item : dataSet) {
                    footer.setField("合计数量", footer.getDouble("合计数量") + item.getDouble("Num_"));
                    footer.setField("合计金额", footer.getDouble("合计金额") + item.getDouble("OriAmount_"));
                }
                int row = template.getRow();
                for (String field : footer.getItems().keySet()) {
                    row++;
                    Object val = footer.getItems().get(field);
                    sheet1.addCell(new Label(0, row, field));
                    sheet1.addCell(new Label(1, row, Double.toString(Utils.roundTo((Double) val, -2))));
                }
            });

            // 输出原来的表格
            super.output(sheet);
            newRow += this.getHeads().size() + dataSet.size() + 6;
            this.setRow(newRow);
        }
    }

    public List<DataSet> getItems() {
        return items;
    }

    public void setItems(List<DataSet> items) {
        this.items = items;
    }

}
