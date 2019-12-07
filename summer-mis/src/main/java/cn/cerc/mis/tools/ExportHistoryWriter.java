package cn.cerc.mis.tools;

import cn.cerc.mis.excel.output.HistoryWriter;
import cn.cerc.mis.excel.output.ExcelTemplate;
import cn.cerc.mis.other.HistoryLevel;
import cn.cerc.mis.other.HistoryRecord;
import cn.cerc.core.IHandle;

public class ExportHistoryWriter implements HistoryWriter {

    @Override
    public void start(Object handle, ExcelTemplate template) {
    }

    @Override
    public void finish(Object handle, ExcelTemplate template) {
        IHandle appHandle = (IHandle) handle;
        String log = String.format("系统已经为您导出: %s.xls", template.getFileName());
        new HistoryRecord(log).setLevel(HistoryLevel.General).save(appHandle);
    }
}
