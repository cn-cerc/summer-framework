package cn.cerc.mis.other;

import cn.cerc.core.Utils;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.mysql.BatchScript;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.ISystemTable;

/**
 * 系统历史日志等级，用户不允许删除，由系统自动定时清理
 */
public enum HistoryLevel {
    // 一般用户的操作日志，一般保存1个月
    General(1),

    // 一般用于各类回算作业记录：保存3个月
    Month3(3),

    // 一般单据内容的变更，如撤消、作废、变更成本单价、交期等，保存1年
    Year1(12),

    // 一般用于各类基本资料数据变更，保存3年
    Year3(36),

    // 永久保存: 系统参数变更，用户帐号的增减变化等
    Forever(0);

    private int month;

    HistoryLevel(int month) {
        this.month = month;
    }

    public int getMonth() {
        return month;
    }

    public void append(IHandle handle, String content) {
        String corpNo = handle.getCorpNo();
        if (corpNo == null || "".equals(corpNo)) {
            // FIXME 此处应该使用 ClassResource
            throw new RuntimeException("生成日志时，公司编号不允许为空！");
        }

        // FIXME 此处应该做进一步抽象
        String userCode = handle.getUserCode();
        ISystemTable systemTable = Application.getSystemTable();
        BatchScript script = new BatchScript(handle);
        script.add("insert into %s (CorpNo_,Level_,Log_,AppUser_,UpdateKey_) values ('%s',%d,'%s','%s','%s')",
                systemTable.getUserLogs(), corpNo, this.getMonth(), Utils.safeString(Utils.copy(content, 1, 80)),
                userCode, Utils.newGuid());
        script.exec();
    }

}
