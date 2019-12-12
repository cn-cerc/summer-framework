package cn.cerc.mis.other;

import cn.cerc.core.IHandle;
import cn.cerc.core.Record;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.client.RemoteService;
import cn.cerc.mis.config.ApplicationProperties;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.ISystemTable;

public class Memory_UserInfo {

    public static int count = 0;

    public static MemoryBuffer get(IHandle handle, String userCode) {
        MemoryBuffer buff = new MemoryBuffer(BufferType.getAccount, userCode);
        if (!buff.isNull()) {
            count++;
            return buff;
        }

        String appRole = ServerConfig.getInstance().getProperty(ApplicationProperties.App_Role_Key,
                ApplicationProperties.App_Role_Master);

        Record record;
        if (ApplicationProperties.App_Role_Master.equals(appRole)) {
            // 主服务器
            ISystemTable systemTable = Application.getBean("systemTable", ISystemTable.class);
            SqlQuery ds = new SqlQuery(handle);
            ds.add("select a.Code_,a.Enabled_,a.Name_,a.SuperUser_,a.DiyRole_,a.RoleCode_,oi.Type_,a.ImageUrl_ ");
            ds.add("from %s a ", systemTable.getUserInfo());
            ds.add("inner join %s oi on a.CorpNo_=oi.CorpNo_ ", systemTable.getBookInfo());
            ds.add("where a.Code_='%s'", userCode);
            ds.open();
            if (ds.eof())
                throw new RuntimeException(String.format("用户代码 %s 不存在!", userCode));
            record = ds.getCurrent();
        } else {
            // 从服务器
            RemoteService svr = new RemoteService(handle, ISystemTable.Master_Book, "ApiUserInfo.getUserInfo");
            if (!svr.exec("UserCode_", userCode)) {
                throw new RuntimeException(svr.getMessage());
            }
            record = svr.getDataOut().getCurrent();
        }

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
