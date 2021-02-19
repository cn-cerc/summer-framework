package cn.cerc.mis.excel.output;

import java.util.List;

import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * 定义BOM导出模版
 * 
 * @author weish
 *
 */
public class BOMTemplate extends ExcelTemplate {
    private List<Column> heads;
    private List<Column> materials;
    private List<Column> makes;
    private DataSet materialDataSet;
    private DataSet makeDataSet;

    public List<Column> getHeads() {
        return heads;
    }

    public void setHeads(List<Column> heads) {
        this.heads = heads;
    }

    @Override
    public void output(WritableSheet sheet) throws RowsExceededException, WriteException {
        // 输出列头
        Record head = this.getDataSet().getHead();
        if (heads != null) {
            for (int lineNo = 0; lineNo < heads.size(); lineNo++) {
                Column column = heads.get(lineNo);
                int row = this.getRow() + lineNo;
                Label item1 = new Label(0, row, column.getName());
                sheet.addCell(item1);
                column.setRecord(head);
                writeColumn(sheet, 1, row, column, null);
            }
            // 设置新的起点行号
            this.setRow(this.getRow() + heads.size());
        }
        // 输出原来的表格
        super.output(sheet);

        this.setRow(this.getHeads().size() + this.getDataSet().size() + 2);
        sheet.addCell(new Label(0, this.getRow(), "材料清单："));
        this.setRow(this.getRow() + 1);
        this.getColumns().clear();
        this.getColumns().addAll(materials);
        this.setDataSet(materialDataSet);
        // 输出原来的表格
        super.output(sheet);

        this.setRow(this.getRow() + materialDataSet.size() + 2);
        sheet.addCell(new Label(0, this.getRow(), "制成清单："));
        this.setRow(this.getRow() + 1);
        this.getColumns().clear();
        this.getColumns().addAll(makes);
        this.setDataSet(makeDataSet);
        // 输出原来的表格
        super.output(sheet);
    }

    public List<Column> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Column> materials) {
        this.materials = materials;
    }

    public List<Column> getMakes() {
        return makes;
    }

    public void setMakes(List<Column> makes) {
        this.makes = makes;
    }

    public DataSet getMaterialDataSet() {
        return materialDataSet;
    }

    public void setMaterialDataSet(DataSet materialDataSet) {
        this.materialDataSet = materialDataSet;
    }

    public DataSet getMakeDataSet() {
        return makeDataSet;
    }

    public void setMakeDataSet(DataSet makeDataSet) {
        this.makeDataSet = makeDataSet;
    }
}
