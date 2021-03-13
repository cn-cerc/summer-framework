package cn.cerc.mis.excel.output;

import cn.cerc.core.ClassResource;
import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.config.ApplicationConfig;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WriteException;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 批次导出单据明细
 */
public class BatchFormTemplate extends FormTemplate {
    private static final ClassResource res = new ClassResource(BatchFormTemplate.class, SummerMIS.ID);
    DecimalFormat df = new DecimalFormat(ApplicationConfig.getPattern());

    List<DataSet> items;

    @Override
    public void output(WritableSheet sheet) throws WriteException {
        int newRow = 0;
        for (DataSet dataSet : items) {
            this.setDataSet(dataSet);
            this.setFooter((template, sheet1) -> {
                Record footer = new Record();
                for (Record item : dataSet) {
                    footer.setField(res.getString(1, "合计数量"), footer.getDouble(res.getString(1, "合计数量")) + item.getDouble("Num_"));
                    footer.setField(res.getString(2, "合计金额"), footer.getDouble(res.getString(2, "合计金额")) + item.getDouble("OriAmount_"));
                }
                int row = template.getRow();
                for (String field : footer.getItems().keySet()) {
                    row++;
                    Object val = footer.getItems().get(field);
                    sheet1.addCell(new Label(0, row, field));
                    sheet1.addCell(new Label(1, row, df.format(val)));
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
