package cn.cerc.mis.tools;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.db.core.IHandle;
import cn.cerc.db.mysql.BuildQuery;
import cn.cerc.mis.core.Handle;

public class FieldUseCheck extends Handle {
    private List<String> items = new ArrayList<>();

    public FieldUseCheck(IHandle handle) {
        this.setHandle(handle);
    }

    public boolean exists(String fieldValue) {
        for (String key : items) {
            String[] args = key.split(":");
            if (checkTable(args[0], args[1], fieldValue)) {
                return true;
            }
        }
        return false;
    }

    public FieldUseCheck add(String tableCode, String fieldCode) {
        items.add(tableCode + ":" + fieldCode);
        return this;
    }

    private boolean checkTable(String tableCode, String fieldCode, String fieldValue) {
        //FIXME 此处应该进一步抽象处理 
        BuildQuery bq = new BuildQuery(this);
        bq.byField("CorpNo_", handle.getCorpNo());
        bq.byParam(String.format("%s=N'%s'", fieldCode, fieldValue));
        bq.setMaximum(1);
        bq.add("select %s * ");
        bq.add("from %s ", tableCode);
        bq.open();
        return !bq.getDataSet().eof();
    }
}
