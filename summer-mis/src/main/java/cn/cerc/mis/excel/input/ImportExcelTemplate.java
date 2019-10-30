package cn.cerc.mis.excel.input;

import java.util.List;

public class ImportExcelTemplate {
    private String fileName;
    private List<ImportColumn> columns;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<ImportColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<ImportColumn> columns) {
        this.columns = columns;
    }

    public void addColumn(ImportColumn column) {
        columns.add(column);
    }
}
