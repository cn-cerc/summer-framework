package cn.cerc.mis.tools;

import cn.cerc.db.core.IHandle;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.IOptionReader;
import cn.cerc.mis.excel.output.IAccreditManager;
import cn.cerc.mis.other.BufferType;
import cn.cerc.mis.other.MemoryBuffer;

public class ExportAccreditManager implements IAccreditManager {
    private String securityCode;
    private String describe;

    @Override
    public boolean isPass(Object handle) {
        if (securityCode == null) {
            throw new RuntimeException("securityCode is null");
        }
        IHandle appHandle = (IHandle) handle;
        return "on".equals(userOptionEnabled(appHandle, securityCode));
    }

    private String userOptionEnabled(IHandle handle, String optCode) {
        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getUserOption, handle.getUserCode(), optCode)) {
            if (buff.isNull()) {
                IOptionReader reader = Application.getDefaultBean(handle, IOptionReader.class);
                String value = reader.getUserValue(handle.getUserCode(), optCode, "");
                buff.setField("Value_", value);
            }
            return buff.getString("Value_");
        }
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    @Override
    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

}
