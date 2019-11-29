package cn.cerc.mis.other;

import cn.cerc.core.IHandle;
import cn.cerc.core.Utils;
import cn.cerc.db.mysql.BatchScript;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.ISystemTable;

public class HistoryRecord {
    private IHandle handle;
    private StringBuilder content = new StringBuilder();
    private HistoryLevel level = HistoryLevel.General;

    public HistoryRecord() {
    }

    public HistoryRecord(String content) {
        this.append(content);
    }

    public IHandle getHandle() {
        return handle;
    }

    public HistoryLevel getLevel() {
        return level;
    }

    public HistoryRecord setLevel(HistoryLevel level) {
        this.level = level;
        return this;
    }

    public void setHandle(IHandle handle) {
        this.handle = handle;
    }

    public String getContent() {
        return content.toString();
    }

    public HistoryRecord append(String content) {
        this.content.append(content);
        return this;
    }

    public void save(IHandle handle) {
        String corpNo = handle.getCorpNo();
        if (corpNo == null || "".equals(corpNo)) {
            throw new RuntimeException("生成日志时，公司编号不允许为空！");
        }

        String userCode = handle.getUserCode();
        String log = content.toString();
        int mth = 0;

        switch (this.level) {
        case General:
            mth = 1;
            break;
        case Month3:
            mth = 3;
            break;
        case Year1:
            mth = 12;
            break;
        case Year3:
            mth = 36;
            break;
        default:
            mth = 0;
        }
        ISystemTable systemTable = Application.getBean("systemTable", ISystemTable.class);
        BatchScript bs = new BatchScript(handle);
        bs.add("insert into %s (CorpNo_,Level_,Log_,AppUser_,UpdateKey_) values ('%s',%d,'%s','%s','%s')",
                systemTable.getUserLogs(), corpNo, mth, Utils.safeString(Utils.copy(log, 1, 80)), userCode,
                Utils.newGuid());
        bs.exec();
    }
}
