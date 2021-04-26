package cn.cerc.mis.custom;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.cerc.core.ClassResource;
import cn.cerc.core.ISession;
import cn.cerc.core.TDateTime;
import cn.cerc.core.Utils;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.core.ISystemTable;
import cn.cerc.mis.language.ILanguageReader;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LanguageReaderDefault implements ILanguageReader, IHandle {
    private static final ClassResource res = new ClassResource(LanguageReaderDefault.class, SummerMIS.ID);
    @Autowired
    private ISystemTable systemTable;
    private ISession session;

    @Override
    public int loadDictionary(Map<String, String> items, String langId) {
        if (Utils.isEmpty(langId)) {
            throw new RuntimeException(res.getString(1, "语言类型不允许为空"));
        }

        SqlQuery dsLang = new SqlQuery(this);
        dsLang.add("select key_,value_ from %s", systemTable.getLanguage());
        dsLang.add("where lang_='%s'", langId);
        dsLang.open();
        while (dsLang.fetch()) {
            items.put(dsLang.getString("key_"), dsLang.getString("value_"));
        }

        return items.size();
    }

    @Override
    public String getOrSet(String langId, String key) {
        if (Utils.isEmpty(langId)) {
            throw new RuntimeException(res.getString(1, "语言类型不允许为空"));
        }
        if (Utils.isEmpty(key)) {
            throw new RuntimeException(res.getString(2, "翻译文字不允许为空"));
        }

        SqlQuery dsLang = new SqlQuery(this);
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
            return key;
        } else {
            return dsLang.getString("Value_");
        }
    }

    @Override
    public ISession getSession() {
        return session;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
    }

}
