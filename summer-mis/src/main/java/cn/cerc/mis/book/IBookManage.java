package cn.cerc.mis.book;

import cn.cerc.core.ClassResource;
import cn.cerc.core.TDateTime;
import cn.cerc.db.core.IHandle;
import cn.cerc.mis.SummerMIS;

public interface IBookManage extends IHandle {
    ClassResource res = new ClassResource(IBookManage.class, SummerMIS.ID);

    // 是否为批处理模式（月度回算 or 单据过帐)
    boolean isBatchMode();

    // 设置过帐日期范围，在用于回算时令 force=false, 在用于查询或超过2个月的记录要进行过帐时，令fore=true
    void setDateRange(TDateTime beginDate, TDateTime endDate, boolean forceExecute);

    // 取得回算年月
    default String getBookMonth() {
        TDateTime dateFrom = getDateFrom();
        if (dateFrom == null) {
            throw new RuntimeException(res.getString(1, "帐本年月不允许为空！"));
        }
        return dateFrom.getYearMonth();
    }

    default void setBookMonth(String beginYearMonth) {
        // 传入日期年月大于当前年月则默认为当前年月
        if (beginYearMonth.compareTo(TDateTime.now().getYearMonth()) > 0) {
            beginYearMonth = TDateTime.now().getYearMonth();
        }
        setDateRange(TDateTime.fromYearMonth(beginYearMonth), TDateTime.now(), false);
    }

    // 是否预览变更而不保存
    boolean isPreviewUpdate();

    // 是否指定料号回算
    String getPartCode();

    // 取得开始日期
    TDateTime getDateFrom();

    // 取得结束日期
    TDateTime getDateTo();

    // 取得期初年月
    String getInitMonth();

    // 返回数据容器
    BookDataList getDatas();
}
