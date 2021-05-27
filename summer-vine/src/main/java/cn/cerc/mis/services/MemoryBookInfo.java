package cn.cerc.mis.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ClassResource;
import cn.cerc.core.Record;
import cn.cerc.core.Utils;
import cn.cerc.db.core.IHandle;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.cache.MemoryListener;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.other.BookVersion;

public class MemoryBookInfo {
    private static final Logger log = LoggerFactory.getLogger(MemoryBookInfo.class);
    private static final ClassResource res = new ClassResource(MemoryBookInfo.class, SummerMIS.ID);

    public static BookInfoRecord get(IHandle handle, String corpNo) {
        if (Utils.isEmpty(corpNo)) {
            throw new RuntimeException("corpNo is null.");
        }
        
        ICorpInfoReader reader = Application.getBean(ICorpInfoReader.class);
        Record record = reader.getCorpInfo(handle, corpNo);
        
        BookInfoRecord item = new BookInfoRecord();
        if (record == null) {
            log.error(String.format("corpNo %s not find.", corpNo));
            item.setCode(corpNo);
            return item;
        }

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

        return item;
    }

    /**
     * 检查帐套代码是否是系统生成
     */
    public static boolean exist(IHandle handle, String corpNo) {
        BookInfoRecord book = MemoryBookInfo.get(handle, corpNo);
        return !Utils.isEmpty(book.getShortName());
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

    public static void clear(IHandle handle, String corpNo) {
        MemoryListener.updateCache("corpInfoReaderDefault", corpNo);
    }

}
