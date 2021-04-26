package cn.cerc.mis.core;

import cn.cerc.db.core.IHandle;
import cn.cerc.mis.other.BufferType;
import cn.cerc.mis.other.MemoryBuffer;

//TODO IBookOption 抽象有误，需要进行一步改进
public interface IBookOption extends IVineOption {

    default String getBookId(IHandle handle) {
        return handle.getCorpNo();
    }

    default String getValue(IHandle handle) {
        return getValue(handle, "");
    }

    default String getValue(IHandle handle, String def) {
        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getVineOptions, handle.getCorpNo(), getKey())) {
            if (buff.isNull()) {
                IOptionReader reader = Application.getDefaultBean(handle, IOptionReader.class);
                String value = reader.getCorpValue(handle.getCorpNo(), getKey(), def);
                buff.setField("Value_", value);
            }
            return buff.getString("Value_");
        }
    }
}
