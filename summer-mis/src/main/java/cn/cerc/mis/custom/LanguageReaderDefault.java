package cn.cerc.mis.custom;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.cerc.core.ClassResource;
import cn.cerc.core.TDateTime;
import cn.cerc.core.Utils;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.cache.CacheResetMode;
import cn.cerc.mis.cache.IMemoryCache;
import cn.cerc.mis.core.ISystemTable;
import cn.cerc.mis.language.ILanguageReader;

@Component
public class LanguageReaderDefault implements ILanguageReader, IMemoryCache {
    private static final ClassResource res = new ClassResource(LanguageReaderDefault.class, SummerMIS.ID);
    @Autowired
    private ISystemTable systemTable;
    private Map<String, String> buff;
    private String beanName;

    @Override
    public int loadDictionary(IHandle handle, Map<String, String> items, String langId) {
        if (Utils.isEmpty(langId)) {
            throw new RuntimeException(res.getString(1, "语言类型不允许为空"));
        }

        if (buff == null) {
            synchronized (this) {
                buff = new HashMap<>();
                SqlQuery dsLang = new SqlQuery(handle);
                dsLang.add("select key_,value_ from %s", systemTable.getLanguage());
                dsLang.add("where lang_='%s'", langId);
                dsLang.open();
                while (dsLang.fetch()) {
                    buff.put(dsLang.getString("key_"), dsLang.getString("value_"));
                }
            }
        }

        items.putAll(buff);
        return items.size();
    }

    @Override
    public String getOrSet(IHandle handle, String langId, String key) {
        if (Utils.isEmpty(langId))
            throw new RuntimeException(res.getString(1, "语言类型不允许为空"));
        if (Utils.isEmpty(key))
            throw new RuntimeException(res.getString(2, "翻译文字不允许为空"));
        if (buff != null && buff.containsKey(key))
            return buff.get(key);

        synchronized (this) {
            String result = key;
            SqlQuery dsLang = new SqlQuery(handle);
            dsLang.add("select * from %s", systemTable.getLanguage());
            dsLang.add("where lang_='%s'", langId);
            dsLang.add("and key_='%s'", key);
            dsLang.open();
            if (dsLang.eof()) {
                dsLang.append();
                dsLang.setField("Lang_", langId);
                dsLang.setField("Key_", key);
                dsLang.setField("CreateDate_", TDateTime.now());
                dsLang.setField("CreateUser_", "admin");
                dsLang.setField("UpdateDate_", TDateTime.now());
                dsLang.setField("UpdateUser_", "admin");
                dsLang.post();
            } else {
                result = dsLang.getString("Value_");
            }
            buff.put(key, result);
            return result;
        }
    }

    @Override
    public void resetCache(IHandle handle, CacheResetMode resetType, String param) {
        if (buff != null) {
            buff.clear();
            buff = null;
        }
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public String getBeanName() {
        return beanName;
    }

}
