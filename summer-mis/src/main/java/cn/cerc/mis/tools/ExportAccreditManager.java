package cn.cerc.mis.tools;

import cn.cerc.core.IHandle;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.ISystemTable;
import cn.cerc.mis.excel.output.AccreditManager;
import cn.cerc.mis.other.BufferType;
import cn.cerc.mis.other.MemoryBuffer;

public class ExportAccreditManager implements AccreditManager {
    private String securityCode;
    private String describe;

    @Override
    public boolean isPass(Object handle) {
        if (securityCode == null)
            throw new RuntimeException("securityCode is null");
        IHandle appHandle = (IHandle) handle;
        return UserOptionEnabled(appHandle, securityCode).equals("on");
    }

    private String UserOptionEnabled(IHandle handle, String code) {
        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getUserOption, handle.getUserCode(), code)) {
            if (buff.isNull()) {
                ISystemTable systemTable = Application.getBean("systemTable", ISystemTable.class);
                SqlQuery ds = new SqlQuery(handle);
                ds.add("select Value_ from %s", systemTable.getUserOptions());
                ds.add("where UserCode_='%s' and Code_='%s'", handle.getUserCode(), code);
                ds.open();
                if (!ds.eof())
                    buff.setField("Value_", ds.getString("Value_"));
                else
                    buff.setField("Value_", "");
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
