package cn.cerc.mis.tools;

import cn.cerc.core.ClassResource;
import cn.cerc.db.core.IHandle;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.excel.output.ExcelTemplate;
import cn.cerc.mis.excel.output.HistoryWriter;
import cn.cerc.mis.other.HistoryLevel;


public class ExportHistoryWriter implements HistoryWriter {
    private static final ClassResource res = new ClassResource(ExportHistoryWriter.class, SummerMIS.ID);

    @Override
    public void start(Object handle, ExcelTemplate template) {
    }

    @Override
    public void finish(Object handle, ExcelTemplate template) {
        IHandle appHandle = (IHandle) handle;
        String log = String.format(res.getString(1, "系统已经为您导出: %s.xls"), template.getFileName());
        HistoryLevel.General.append(appHandle, log);
    }
}
