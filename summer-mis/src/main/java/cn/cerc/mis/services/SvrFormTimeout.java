package cn.cerc.mis.services;

import cn.cerc.core.Record;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.core.CustomService;

@Deprecated
//FIXME SvrFormTimeout 对象应该从框架中移出
public class SvrFormTimeout extends CustomService {

    // 保存Form用户请求
    public boolean save() {
        Record headIn = getDataIn().getHead();
        SqlQuery ds = new SqlQuery(handle);
        ds.setMaximum(0);
        ds.add("select * from %s", systemTable.getPageLogs());
        ds.open();
        ds.append();
        ds.setField("CorpNo_", this.getCorpNo());
        ds.setField("Page_", headIn.getString("pageCode"));
        ds.setField("DataIn_", headIn.getString("dataIn"));
        ds.setField("TickCount_", headIn.getDouble("tickCount"));
        ds.setField("AppUser_", this.getUserCode());
        ds.post();

        return true;
    }

}
