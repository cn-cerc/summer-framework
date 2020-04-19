package cn.cerc.mis.excel.output;

public interface HistoryWriter {
    void start(Object handle, ExcelTemplate template);

    void finish(Object handle, ExcelTemplate template);
}
