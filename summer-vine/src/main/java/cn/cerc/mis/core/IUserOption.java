package cn.cerc.mis.core;

import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import cn.cerc.db.core.IHandle;
import cn.cerc.mis.other.BufferType;
import cn.cerc.mis.other.MemoryBuffer;

public interface IUserOption extends IVineOption {

    default String getOption(IHandle handle) {
        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getUserOption, handle.getUserCode(), getKey())) {
            if (buff.isNull()) {
                CenterService svr = new CenterService(handle);
                svr.setService("ApiUserInfo.getOptionValue");
                Record headIn = svr.getDataIn().getHead();
                headIn.setField("UserCode_", handle.getUserCode());
                headIn.setField("Code_", getKey());
                if (!svr.exec()) {
                    throw new RuntimeException(svr.getMessage());
                }

                DataSet ds = svr.getDataOut();
                if (!ds.eof()) {
                    buff.setField("Value_", ds.getString("Value_"));
                } else {
                    buff.setField("Value_", "");
                }
            }
            return buff.getString("Value_");
        }
    }
}
