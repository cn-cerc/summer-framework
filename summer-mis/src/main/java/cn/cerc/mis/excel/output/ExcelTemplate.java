package cn.cerc.mis.excel.output;

import java.util.Date;
import java.util.List;

import cn.cerc.core.DataSet;
import cn.cerc.core.TDate;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelTemplate {
    private String fileName;
    private List<Column> columns;
    private AccreditManager accreditManager;
    private HistoryWriter historyWriter;
    private DataSet dataSet;
    private DateFormat df1 = new DateFormat("yyyy-MM-dd");
    private DateFormat df2 = new DateFormat("yyyy-MM-dd HH:mm:ss");
    private int row = 0;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public void addColumn(Column column) {
        columns.add(column);
    }

    public AccreditManager getAccreditManager() {
        return accreditManager;
    }

    public void setAccreditManager(AccreditManager accredit) {
        this.accreditManager = accredit;
    }

    public HistoryWriter getHistoryWriter() {
        return historyWriter;
    }

    public void setHistoryWriter(HistoryWriter historyWriter) {
        this.historyWriter = historyWriter;
    }

    public void output(WritableSheet sheet) throws RowsExceededException, WriteException {
        // 输出列头
        for (int col = 0; col < columns.size(); col++) {
            Column column = columns.get(col);
            Label item = new Label(col, row, column.getName());
            sheet.addCell(item);
        }

        // 输出列数据
        if (dataSet != null) {
            dataSet.first();
            while (dataSet.fetch()) {
                row++;
                for (int col = 0; col < columns.size(); col++) {
                    Column column = columns.get(col);
                    column.setRecord(dataSet.getCurrent());
                    writeColumn(sheet, col, row, column);
                }
            }
        }
    }

    protected void writeColumn(WritableSheet sheet, int col, int row, Column column)
            throws WriteException, RowsExceededException {
        if (column instanceof NumberColumn) {
            jxl.write.Number item = new jxl.write.Number(col, row, (double) column.getValue());
            sheet.addCell(item);
        } else if (column instanceof DateColumn) {
            TDate day = (TDate) column.getValue();
            DateTime item = new DateTime(col, row, day.getData(), new WritableCellFormat(df1));
            sheet.addCell(item);
        } else if (column instanceof DateTimeColumn) {
            DateTime item = new DateTime(col, row, (Date) column.getValue(), new WritableCellFormat(df2));
            sheet.addCell(item);
        } else {
            Label item = new Label(col, row, column.getValue().toString());
            sheet.addCell(item);
        }
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }
}
