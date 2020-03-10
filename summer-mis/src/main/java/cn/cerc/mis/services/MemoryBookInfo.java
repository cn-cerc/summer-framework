package cn.cerc.mis.services;

import cn.cerc.core.IHandle;
import cn.cerc.core.Record;
import cn.cerc.core.Utils;
import cn.cerc.db.cache.Redis;
import cn.cerc.mis.client.IServiceProxy;
import cn.cerc.mis.client.ServiceFactory;
import cn.cerc.mis.other.BookVersion;
import cn.cerc.mis.other.BufferType;
import com.google.gson.Gson;

public class MemoryBookInfo {

    private static final String buffVersion = "5";

    public static BookInfoRecord get(IHandle handle, String corpNo) {
        Gson gson = new Gson();
        String tmp = Redis.get(getBuffKey(corpNo));
        if (Utils.isNotEmpty(tmp)) {
            return gson.fromJson(tmp, BookInfoRecord.class);
        }

        IServiceProxy svr = ServiceFactory.get(handle);
        svr.setService("SvrBookInfo.getRecord");
        if (!svr.exec("corpNo", corpNo)) {
            return null;
        }
        Record record = svr.getDataOut().getHead();

        BookInfoRecord item = new BookInfoRecord();
        item.setCode(record.getString("CorpNo_"));
        item.setShortName(record.getString("ShortName_"));
        item.setName(record.getString("Name_"));
        item.setAddress(record.getString("Address_"));
        item.setTel(record.getString("Tel_"));
        item.setManagerPhone(record.getString("ManagerPhone_"));
        item.setStartHost(record.getString("StartHost_"));
        item.setContact(record.getString("Contact_"));
        item.setAuthentication(record.getBoolean("Authentication_"));
        item.setStatus(record.getInt("Status_"));
        item.setCorpType(record.getInt("Type_"));
        item.setIndustry(record.getString("Industry_"));

        Redis.set(getBuffKey(corpNo), gson.toJson(item));
        return item;
    }

    /**
     * @param handle 环境变量
     * @param corpNo 帐套代码
     * @return 返回帐套状态
     */
    public static int getStatus(IHandle handle, String corpNo) {
        BookInfoRecord item = get(handle, corpNo);
        if (item == null) {
            throw new RuntimeException(String.format("没有找到注册的帐套  %s ", corpNo));
        }
        return item.getStatus();
    }

    /**
     * @param handle 环境变量
     * @return 返回当前帐套的版本类型
     */
    public static BookVersion getBookType(IHandle handle) {
        String corpNo = handle.getCorpNo();
        BookInfoRecord item = get(handle, corpNo);
        if (item == null) {
            throw new RuntimeException(String.format("没有找到注册的帐套  %s ", corpNo));
        }
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
        if (item == null) {
            throw new RuntimeException(String.format("没有找到注册的帐套  %s ", corpNo));
        }
        return item.getShortName();
    }

    public static String getIndustry(IHandle handle, String corpNo) {
        BookInfoRecord item = get(handle, corpNo);
        if (item == null) {
            throw new RuntimeException(String.format("没有找到注册的帐套  %s ", corpNo));
        }
        return item.getIndustry();
    }

    public static void clear(String corpNo) {
        Redis.delete(getBuffKey(corpNo));
    }

    private static String getBuffKey(String corpNo) {
        return String.format("%s.%s.%s", BufferType.getOurInfo, corpNo, buffVersion);
    }
}
