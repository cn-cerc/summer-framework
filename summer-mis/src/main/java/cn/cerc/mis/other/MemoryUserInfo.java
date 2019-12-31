package cn.cerc.mis.other;

import cn.cerc.core.IHandle;
import cn.cerc.core.Record;
import cn.cerc.mis.client.IServiceProxy;
import cn.cerc.mis.client.ServiceFactory;

/**
 * FIXME 2019-12-17 改为从项目本身创建
 */
public class MemoryUserInfo {

    public static int count = 0;

    public static MemoryBuffer get(IHandle handle, String userCode) {
        MemoryBuffer buff = new MemoryBuffer(BufferType.getAccount, userCode);
        if (!buff.isNull()) {
            count++;
            return buff;
        }

        IServiceProxy svr = ServiceFactory.get(handle, ServiceFactory.Public, "ApiUserInfo.getUserInfo");
        if (!svr.exec("UserCode_", userCode)) {
            throw new RuntimeException(svr.getMessage());
        }
        Record record = svr.getDataOut().getCurrent();

        buff.setField("Name_", record.getString("Name_"));
        buff.setField("Enabled_", record.getInt("Enabled_"));
        buff.setField("SuperUser_", record.getBoolean("SuperUser_"));
        buff.setField("ImageUrl_", record.getString("ImageUrl_"));
        if (record.getBoolean("DiyRole_")) {
            buff.setField("RoleCode_", record.getString("Code_"));
        } else {
            buff.setField("RoleCode_", record.getString("RoleCode_"));
        }
        buff.setField("CorpType_", "" + record.getInt("Type_") + ",");
        return buff;
    }

    public static void clear(String userCode) {
        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getAccount, userCode)) {
            buff.clear();
        }
    }

}
