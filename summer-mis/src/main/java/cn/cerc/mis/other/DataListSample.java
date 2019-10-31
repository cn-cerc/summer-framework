package cn.cerc.mis.other;

import cn.cerc.core.IHandle;
import cn.cerc.db.cache.Redis;
import cn.cerc.db.mysql.MysqlConnection;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.ISystemTable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 以用户表为原型，增加从缓存读取数据的模板
 */
public class DataListSample implements IDataList {
    private static final Logger log = LoggerFactory.getLogger(DataListSample.class);
    private static final int Version = 4;
    private IHandle handle;
    private Map<String, DataRecordSample> buff = new HashMap<>();
    private String buffKey;

    private static final String ShowInUP = "ShowInUP";
    private static final String ShowOutUP = "ShowOutUP";
    private static final String ShowWholesaleUP = "ShowWholesaleUP";
    private static final String ShowBottomUP = "ShowBottomUP";

    public DataListSample(IHandle handle) {
        super();
        this.handle = handle;
        if (handle != null)
            buffKey = String.format("%d.%s.%s.%d", BufferType.getObject.ordinal(), handle.getCorpNo(),
                    this.getClass().getName(), Version);
    }

    public String getNameDef(String key) {
        // 不允许用户帐号为空
        if (key == null || "".equals(key))
            return "";

        // 从缓存中取回值
        DataRecordSample result = get(key);
        return result == null ? key : result.getName();
    }

    public DataRecordSample get(String userCode) {
        if (userCode == null || "".equals(userCode))
            throw new RuntimeException("用户代码不允许为空！");

        // 初始化缓存
        this.init();

        // 从缓存中取回值
        return buff.get(userCode);
    }

    private void init() {
        if (buff.size() > 0)
            return;

        // 从缓存中读取
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        String data = Redis.get(buffKey);
        if (data != null && !"".equals(data)) {
            Type type = new TypeToken<Map<String, DataRecordSample>>() {
            }.getType();
            Map<String, DataRecordSample> items = gson.fromJson(data, type);
            for (String key : items.keySet()) {
                buff.put(key, items.get(key));
            }
            log.debug(this.getClass().getName() + " 缓存成功！");
            return;
        }

        ISystemTable systemTable = Application.getBean("systemTable", ISystemTable.class);
        // 从数据库中读取
        SqlQuery ds = new SqlQuery(handle);
        ds.add("select ID_,CorpNo_,Code_,Name_,QQ_,Mobile_,SuperUser_,");
        ds.add("LastRemindDate_,EmailAddress_,RoleCode_,ProxyUsers_,Enabled_,DiyRole_ ");
        ds.add("from %s ", systemTable.getUserInfo());
        ds.add("where CorpNo_='%s'", handle.getCorpNo());
        ds.open();
        while (ds.fetch()) {
            String key = ds.getString("Code_");
            DataRecordSample value = new DataRecordSample();

            value.setId(ds.getString("ID_"));
            value.setCorpNo(ds.getString("CorpNo_"));
            value.setCode(ds.getString("Code_"));
            value.setName(ds.getString("Name_"));
            value.setHeadImgAdd(ds.getString("HeadimgAdd_"));
            Map<String, Integer> priceValue = getPriceValue(ds.getString("Code_"));
            value.setShowInUP(priceValue.get(ShowInUP));
            value.setShowOutUP(priceValue.get(ShowOutUP));
            value.setShowWholesaleUP(priceValue.get(ShowWholesaleUP));
            value.setShowBottomUP(priceValue.get(ShowBottomUP));

            value.setQq(ds.getString("QQ_"));
            value.setMobile(ds.getString("Mobile_"));
            value.setAdmin(ds.getBoolean("SuperUser_"));
            value.setLastRemindDate(ds.getDateTime("LastRemindDate_").getDate());
            value.setEmail(ds.getString("EmailAddress_"));
            if (ds.getBoolean("DiyRole_"))
                value.setRoleCode(ds.getString("Code_"));
            else
                value.setRoleCode(ds.getString("RoleCode_"));
            value.setProxyUsers(ds.getString("ProxyUsers_"));
            value.setEnabled(ds.getBoolean("Enabled_"));
            buff.put(key, value);
        }

        // 存入到缓存中
        Redis.set(buffKey, gson.toJson(buff));
        log.debug(this.getClass().getName() + " 缓存初始化！");
    }

    private Map<String, Integer> getPriceValue(String userCode) {
        Map<String, Integer> value = new HashMap<>();
        value.put(ShowInUP, 0);
        value.put(ShowOutUP, 0);
        value.put(ShowWholesaleUP, 0);
        value.put(ShowBottomUP, 0);

        ISystemTable systemTable = Application.getBean("systemTable", ISystemTable.class);
        SqlQuery ds = new SqlQuery(handle);
        ds.add("select Code_,Value_ from %s ", systemTable.getUserOptions());
        ds.add("where UserCode_='%s' and (Code_='%s' or Code_='%s' or Code_='%s' or Code_='%s')", userCode, ShowInUP,
                ShowOutUP, ShowWholesaleUP, ShowBottomUP);
        ds.open();
        while (ds.fetch())
            value.put(ds.getString("Code_"), ds.getInt("Value_"));

        return value;
    }

    @Override
    public boolean exists(String key) {
        this.init();
        return buff.get(key) != null;
    }

    @Override
    public void clear() {
        Redis.delete(buffKey);
    }

    /*
     * 切换帐号到指定的公司别
     */
    public void changeCorpNo(IHandle handle, String corpNo, String userCode, String roleCode)
            throws UserNotFindException {
        String buffKey = String.format("%d.%s.%s.%d", BufferType.getObject.ordinal(), corpNo, this.getClass().getName(),
                Version);
        Redis.delete(buffKey);

        ISystemTable systemTable = Application.getBean("systemTable", ISystemTable.class);
        SqlQuery ds = new SqlQuery(handle);
        ds.add("select ID_ from %s where Code_='%s'", systemTable.getUserInfo(), userCode);
        ds.open();
        if (ds.eof())
            throw new UserNotFindException(userCode);

        MysqlConnection conn = (MysqlConnection) handle.getProperty(MysqlConnection.sessionId);
        String sql = String.format("update %s set CorpNo_='%s',ShareAccount_=1 where Code_='%s'",
                systemTable.getUserInfo(), corpNo, userCode);
        conn.execute(sql);

        sql = String.format("update %s set Name_='%s' where UserCode_='%s' and Code_='GroupCode'",
                systemTable.getUserOptions(), roleCode, userCode);
        conn.execute(sql);

        log.info(String.format("%s 已被切换到 corpNo=%s, roleCode=%s", userCode, corpNo, roleCode));
    }

}
