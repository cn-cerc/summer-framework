package cn.cerc.mis.core;

import cn.cerc.core.DataSet;
import cn.cerc.core.IHandle;
import cn.cerc.core.Record;
import cn.cerc.mis.client.IServiceProxy;
import cn.cerc.mis.client.ServiceFactory;
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
                IServiceProxy svr = ServiceFactory.get(handle);
                svr.setService("ApiBookOption.getValue");
                Record headIn = svr.getDataIn().getHead();
                headIn.setField("CorpNo_", handle.getCorpNo());
                headIn.setField("Code_", getKey());
                if (!svr.exec()) {
                    throw new RuntimeException(svr.getMessage());
                }

                DataSet dataOut = svr.getDataOut();
                if (dataOut.eof()) {
                    buff.setField("Value_", def);
                } else {
                    buff.setField("Value_", dataOut.getString("Value_"));
                }
            }
            return buff.getString("Value_");
        }
    }
}
