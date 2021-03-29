package cn.cerc.mis.tools;

import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import cn.cerc.db.core.IHandle;
import cn.cerc.mis.core.CenterService;
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
                //FIXME 此处应该进一步抽象处理
                CenterService svr = new CenterService(handle);
                svr.setService("ApiUserOption.getOptValue");
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
