package cn.cerc.mis.services;

import com.google.gson.Gson;

import cn.cerc.mis.core.LocalService;
import cn.cerc.mis.other.BookVersion;
import cn.cerc.mis.other.BufferType;
import cn.cerc.db.cache.Redis;
import cn.cerc.core.IHandle;
import cn.cerc.core.Record;

public class MemoryBookInfo {
    private static final String buffVersion = "4";

    public static BookInfoRecord get(IHandle handle, String corpNo) {
        String tmp = Redis.get(getBuffKey(corpNo));
        if (tmp == null || "".equals(tmp)) {
            LocalService svr = new LocalService(handle, "SvrBookInfo.getRecord");
            if (!svr.exec("corpNo", corpNo))
                return null;

            BookInfoRecord result = new BookInfoRecord();
            Record ds = svr.getDataOut().getHead();
            result.setCode(ds.getString("CorpNo_"));
            result.setShortName(ds.getString("ShortName_"));
            result.setName(ds.getString("Name_"));
            result.setAddress(ds.getString("Address_"));
            result.setTel(ds.getString("Tel_"));
            result.setManagerPhone(ds.getString("ManagerPhone_"));
            result.setStartHost(ds.getString("StartHost_"));
            result.setContact(ds.getString("Contact_"));
            result.setAuthentication(ds.getBoolean("Authentication_"));
            result.setStatus(ds.getInt("Status_"));
            result.setCorpType(ds.getInt("Type_"));
            result.setIndustry(ds.getString("Industry_"));

            Gson gson = new Gson();
            Redis.set(getBuffKey(corpNo), gson.toJson(result));

            return result;
        } else {
            Gson gson = new Gson();
            return gson.fromJson(tmp, BookInfoRecord.class);
        }
    }

    /**
     * 
     * @param handle
     *            环境变量
     * @param corpNo
     *            帐套代码
     * @return 返回帐套状态
     */
    public static int getStatus(IHandle handle, String corpNo) {
        BookInfoRecord item = get(handle, corpNo);
        if (item == null)
            throw new RuntimeException(String.format("没有找到注册的帐套  %s ", corpNo));
        return item.getStatus();
    }

    /**
     * 
     * @param handle
     *            环境变量
     * @return 返回当前帐套的版本类型
     */
    public static BookVersion getBookType(IHandle handle) {
        String corpNo = handle.getCorpNo();
        return getCorpType(handle, corpNo);
    }

    /**
     * 
     * @param handle
     *            环境变量
     * @param corpNo
     *            帐套代码
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
     * 
     * @param handle
     *            环境变量
     * @param corpNo
     *            帐套代码
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
