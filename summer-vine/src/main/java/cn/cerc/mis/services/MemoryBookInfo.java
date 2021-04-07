package cn.cerc.mis.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import cn.cerc.core.ClassResource;
import cn.cerc.core.Record;
import cn.cerc.core.Utils;
import cn.cerc.db.cache.Redis;
import cn.cerc.db.core.IHandle;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.core.CenterService;
import cn.cerc.mis.other.BookVersion;
import cn.cerc.mis.other.BufferType;

public class MemoryBookInfo {
    private static final Logger log = LoggerFactory.getLogger(MemoryBookInfo.class);
    private static final ClassResource res = new ClassResource(MemoryBookInfo.class, SummerMIS.ID);

    private static final String buffVersion = "5";

    public static BookInfoRecord get(IHandle handle, String corpNo) {
        if (Utils.isEmpty(corpNo)) {
            throw new RuntimeException("corpNo is null.");
        }
        Gson gson = new Gson();
        String tmp = Redis.get(getBuffKey(corpNo));
        if (Utils.isNotEmpty(tmp)) {
            return gson.fromJson(tmp, BookInfoRecord.class);
        }

        BookInfoRecord item = new BookInfoRecord();
        CenterService svr = new CenterService(handle);
        svr.setService("SvrBookInfo.getRecord");
        if (!svr.exec("corpNo", corpNo)) {
            log.error(svr.getMessage());
            item.setCode(corpNo);
            return item;
        }
        Record record = svr.getDataOut().getHead();

        item.setCode(record.getString("CorpNo_"));
        item.setShortName(record.getString("ShortName_"));
        item.setName(record.getString("Name_"));
        item.setAddress(record.getString("Address_"));
        item.setTel(record.getString("Tel_"));
        item.setFastTel(record.getString("FastTel_"));
        item.setManagerPhone(record.getString("ManagerPhone_"));
        item.setStartHost(record.getString("StartHost_"));
        item.setContact(record.getString("Contact_"));
        item.setAuthentication(record.getBoolean("Authentication_"));
        item.setStatus(record.getInt("Status_"));
        item.setCorpType(record.getInt("Type_"));
        item.setIndustry(record.getString("Industry_"));
        item.setCurrency(record.getString("Currency_"));
        item.setEmail(record.getString("Email_"));
        item.setFax(record.getString("Fax_"));

        Redis.set(getBuffKey(corpNo), gson.toJson(item));
        return item;
    }

    /**
     * 检查帐套代码是否是系统生成
     */
    public static boolean exist(IHandle handle, String corpNo) {
        BookInfoRecord book = MemoryBookInfo.get(handle, corpNo);
        return Utils.isNotEmpty(book.getShortName());
    }

    /**
     * @param handle 环境变量
     * @param corpNo 帐套代码
     * @return 返回帐套状态
     */
    public static int getStatus(IHandle handle, String corpNo) {
        BookInfoRecord item = get(handle, corpNo);
        if (item == null) {
            throw new RuntimeException(String.format(res.getString(1, "没有找到注册的帐套 %s"), corpNo));
        }
        return item.getStatus();
    }

    /**
     * @param handle 环境变量
     * @return 返回当前帐套的版本类型
     */
    public static BookVersion getBookType(IHandle handle) {
        String corpNo = handle.getCorpNo();
        return getBookType(handle, corpNo);
    }

    /**
     * @param handle 环境变量
     * @param corpNo 帐套代码
     * @return 返回指定帐套的版本类型
     */
    public static BookVersion getBookType(IHandle handle, String corpNo) {
        BookInfoRecord item = get(handle, corpNo);
        if (item == null) {
            throw new RuntimeException(String.format(res.getString(1, "没有找到注册的帐套 %s"), corpNo));
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
            throw new RuntimeException(String.format(res.getString(1, "没有找到注册的帐套 %s"), corpNo));
        }
        return item.getShortName();
    }

    /**
     * @param handle 环境变量
     * @param corpNo 帐套代码
     * @return 返回帐套全称
     */
    public static String getName(IHandle handle, String corpNo) {
        BookInfoRecord item = get(handle, corpNo);
        if (item == null) {
            throw new RuntimeException(String.format(res.getString(1, "没有找到注册的帐套 %s"), corpNo));
        }
        return item.getName();
    }

    public static String getIndustry(IHandle handle, String corpNo) {
        BookInfoRecord item = get(handle, corpNo);
        if (item == null) {
            throw new RuntimeException(String.format(res.getString(1, "没有找到注册的帐套 %s"), corpNo));
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
