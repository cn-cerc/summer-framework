package cn.cerc.mis.services;

import com.google.gson.Gson;

import cn.cerc.core.IHandle;
import cn.cerc.core.Record;
import cn.cerc.core.Utils;
import cn.cerc.db.cache.Redis;
import cn.cerc.mis.client.RemoteService;
import cn.cerc.mis.config.ApplicationProperties;
import cn.cerc.mis.core.ISystemTable;
import cn.cerc.mis.core.LocalService;
import cn.cerc.mis.other.BookVersion;
import cn.cerc.mis.other.BufferType;

public class MemoryBookInfo {

    private static final String buffVersion = "5";

    public static BookInfoRecord get(IHandle handle, String corpNo) {
        Gson gson = new Gson();
        String tmp = Redis.get(getBuffKey(corpNo));
        if (Utils.isNotEmpty(tmp)) {
            return gson.fromJson(tmp, BookInfoRecord.class);
        }

        Record record;
        if (ApplicationProperties.isMaster()) {
            LocalService svr = new LocalService(handle, "SvrBookInfo.getRecord");
            if (!svr.exec("corpNo", corpNo)) {
                return null;
            }
            record = svr.getDataOut().getHead();
        } else {
            RemoteService svr = new RemoteService(handle, ISystemTable.Public, "SvrBookInfo.getRecord");
            if (!svr.exec("corpNo", corpNo)) {
                return null;
            }
            record = svr.getDataOut().getHead();
        }

        BookInfoRecord result = new BookInfoRecord();
        result.setCode(record.getString("CorpNo_"));
        result.setShortName(record.getString("ShortName_"));
        result.setName(record.getString("Name_"));
        result.setAddress(record.getString("Address_"));
        result.setTel(record.getString("Tel_"));
        result.setManagerPhone(record.getString("ManagerPhone_"));
        result.setStartHost(record.getString("StartHost_"));
        result.setContact(record.getString("Contact_"));
        result.setAuthentication(record.getBoolean("Authentication_"));
        result.setStatus(record.getInt("Status_"));
        result.setCorpType(record.getInt("Type_"));
        result.setIndustry(record.getString("Industry_"));

        Redis.set(getBuffKey(corpNo), gson.toJson(result));

        return result;
    }

    /**
     * @param handle 环境变量
     * @param corpNo 帐套代码
     * @return 返回帐套状态
     */
    public static int getStatus(IHandle handle, String corpNo) {
        BookInfoRecord item = get(handle, corpNo);
        if (item == null)
            throw new RuntimeException(String.format("没有找到注册的帐套  %s ", corpNo));
        return item.getStatus();
    }

    /**
     * @param handle 环境变量
     * @return 返回当前帐套的版本类型
     */
    public static BookVersion getBookType(IHandle handle) {
        String corpNo = handle.getCorpNo();
        return getCorpType(handle, corpNo);
    }

    /**
     * @param handle 环境变量
     * @param corpNo 帐套代码
     * @return 返回指定帐套的版本类型
     */
    public static BookVersion getCorpType(IHandle handle, String corpNo) {
        BookInfoRecord item = get(handle, corpNo);
        if (item == null)
            throw new RuntimeException(String.format("没有找到注册的帐套  %s ", corpNo));
        int result = item.getCorpType();
        return BookVersion.values()[result];
    }

    /**
     * @param handle 环境变量
     * @param corpNo 帐套代码
     * @return 返回帐套简称
     */
    public static String getShortName(IHandle handle, String corpNo) {
        BookInfoRecord item = get(handle, corpNo);
        if (item == null)
            throw new RuntimeException(String.format("没有找到注册的帐套  %s ", corpNo));
        return item.getShortName();
    }

    public static String getIndustry(IHandle handle, String corpNo) {
        BookInfoRecord item = get(handle, corpNo);
        if (item == null)
            throw new RuntimeException(String.format("没有找到注册的帐套  %s ", corpNo));
        return item.getIndustry();
    }

    public static void clear(String corpNo) {
        Redis.delete(getBuffKey(corpNo));
    }

    private static String getBuffKey(String corpNo) {
        return String.format("%s.%s.%s", BufferType.getOurInfo, corpNo, buffVersion);
    }
}
