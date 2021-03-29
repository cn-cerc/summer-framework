package cn.cerc.mis.services;

import cn.cerc.core.ClassResource;
import cn.cerc.core.Record;
import cn.cerc.core.TDateTime;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.core.CustomService;
import cn.cerc.mis.core.DataValidateException;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SvrLanguage extends CustomService {
    private static final ClassResource res = new ClassResource(SvrLanguage.class, SummerMIS.ID);

    public boolean downloadAll() throws DataValidateException {
        Record headIn = getDataIn().getHead();
        DataValidateException.stopRun(res.getString(1, "语言类型不允许为空"), !headIn.hasValue("lang_"));

        SqlQuery dslang = new SqlQuery(this);
        dslang.add("select * from %s", systemTable.getLanguage());
        dslang.add("where lang_='%s'", headIn.getString("lang_"));
        dslang.open();
        getDataOut().appendDataSet(dslang);
        return true;
    }

    public boolean download() throws DataValidateException {
        Record headIn = getDataIn().getHead();
        DataValidateException.stopRun(res.getString(1, "语言类型不允许为空"), !headIn.hasValue("lang_"));
        DataValidateException.stopRun(res.getString(2, "翻译文字不允许为空"), !headIn.hasValue("key_"));

        String lang = headIn.getString("lang_");
        String key = headIn.getString("key_");

        SqlQuery dslang = new SqlQuery(this);
        dslang.add("select * from %s", systemTable.getLanguage());
        dslang.add("where lang_='%s'", lang);
        dslang.add("and key_='%s'", key);
        dslang.open();
        if (dslang.eof()) {
            dslang.append();
            dslang.setField("Key_", key);
            dslang.setField("Lang_", lang);
            dslang.setField("CreateDate_", TDateTime.now());
            dslang.setField("CreateUser_", "admin");
            dslang.setField("UpdateDate_", TDateTime.now());
            dslang.setField("UpdateUser_", "admin");
            dslang.post();
            getDataOut().getHead().setField("value", key);
        } else {
            getDataOut().getHead().setField("value", dslang.getString("Value_"));
        }
        return true;
    }
}
