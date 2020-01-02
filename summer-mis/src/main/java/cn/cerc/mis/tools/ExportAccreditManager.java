package cn.cerc.mis.tools;

import cn.cerc.core.DataSet;
import cn.cerc.core.IHandle;
import cn.cerc.core.Record;
import cn.cerc.mis.client.IServiceProxy;
import cn.cerc.mis.client.ServiceFactory;
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
        return UserOptionEnabled(appHandle, securityCode).equals("on");
    }

    private String UserOptionEnabled(IHandle handle, String optCode) {
        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getUserOption, handle.getUserCode(), optCode)) {
            if (buff.isNull()) {
                IServiceProxy svr = ServiceFactory.get(handle, ServiceFactory.Public, "ApiUserOption.getOptValue");
                Record headIn = svr.getDataIn().getHead();
                headIn.setField("UserCode_", handle.getUserCode());
                headIn.setField("OptCode_", optCode);
                if (!svr.exec()) {
                    throw new RuntimeException(svr.getMessage());
                }
                DataSet cdsTmp = svr.getDataOut();
                if (!cdsTmp.eof()) {
                    buff.setField("Value_", cdsTmp.getString("Value_"));
                } else {
                    buff.setField("Value_", "");
                }
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
