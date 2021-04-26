package cn.cerc.mis.core;

import cn.cerc.db.core.IHandle;
import cn.cerc.mis.other.BufferType;
import cn.cerc.mis.other.MemoryBuffer;

public interface IUserOption extends IVineOption {

    default String getOption(IHandle handle) {
        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getUserOption, handle.getUserCode(), getKey())) {
            if (buff.isNull()) {
                IOptionReader reader = Application.getDefaultBean(handle, IOptionReader.class);
                String value = reader.getUserValue(handle.getUserCode(), getKey(), "");
                buff.setField("Value_", value);
            }
            return buff.getString("Value_");
        }
    }
}
