package cn.cerc.mis.other;

/*
 * 系统历史日志等级，用户不允许删除，由系统自动定时清理
 */

public enum HistoryLevel {
    // 一般用户的操作日志，一般保存1个月
    General,
    // 一般用于各类回算作业记录：保存3个月
    Month3,
    // 一般单据内容的变更，如撤消、作废、变更成本单价、交期等，保存1年
    Year1,
    // 一般用于各类基本资料数据变更，保存3年
    Year3,
    // 永久保存: 系统参数变更，用户帐号的增减变化等
    Forever
}
