package cn.cerc.mis.excel.output;

public interface HistoryWriter {
    public void start(Object handle, ExcelTemplate template);

    public void finish(Object handle, ExcelTemplate template);
}
