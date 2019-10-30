package cn.cerc.mis.core;

import cn.cerc.core.IHandle;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.other.BufferType;
import cn.cerc.mis.other.MemoryBuffer;

public interface IUserOption extends IOption {

    default String getOption(IHandle handle) {
        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getUserOption, handle.getUserCode(), getKey())) {
            if (buff.isNull()) {
                ISystemTable systemTable = Application.getBean("systemTable", ISystemTable.class);
                SqlQuery ds = new SqlQuery(handle);
                ds.add("select Value_ from %s", systemTable.getUserOptions());
                ds.add("where UserCode_='%s' and Code_='%s'", handle.getUserCode(), getKey());
                ds.open();
                if (!ds.eof())
                    buff.setField("Value_", ds.getString("Value_"));
                else
                    buff.setField("Value_", "");
            }
            return buff.getString("Value_");
        }
    }
}
