package cn.cerc.mis.core;

import cn.cerc.core.IHandle;
import cn.cerc.db.mysql.BuildQuery;
import cn.cerc.mis.other.BufferType;
import cn.cerc.mis.other.MemoryBuffer;

public interface IBookOption extends IOption {

    default String getBookId(IHandle handle) {
        return handle.getCorpNo();
    }

    default String getValue(IHandle handle) {
        return getValue(handle, "");
    }

    default String getValue(IHandle handle, String def) {
        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getVineOptions, handle.getCorpNo(), getKey())) {
            if (buff.isNull()) {
                ISystemTable systemTable = Application.getBean("systemTable", ISystemTable.class);
                BuildQuery f = new BuildQuery(handle);
                f.add("select Value_ from %s ", systemTable.getBookOptions());
                f.byField("CorpNo_", handle.getCorpNo());
                f.byField("Code_", getKey());
                f.open();
                if (!f.getDataSet().eof())
                    buff.setField("Value_", f.getDataSet().getString("Value_"));
                else
                    buff.setField("Value_", def);

            }
            return buff.getString("Value_");
        }
    }
}
