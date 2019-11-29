package cn.cerc.mis.language;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.IHandle;
import cn.cerc.core.TDateTime;
import cn.cerc.core.Utils;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.ISystemTable;

public class R {
    private static final Logger log = LoggerFactory.getLogger(R.class);

    public static String getLanguage(IHandle handle) {
        Object temp = handle.getProperty(Application.deviceLanguage);
        if (temp == null || "".equals(temp)) {
            log.debug("handle language is null");
            Object request = handle.getProperty("request");
            if (request != null) {
                log.debug(request.getClass().getName());
                if (request instanceof HttpServletRequest) {
                    HttpServletRequest req = (HttpServletRequest) request;
                    temp = req.getSession().getAttribute(Application.deviceLanguage);
                    log.debug("session language value " + temp);
                }
            }
        }
        String language = temp == null ? Application.getLangage() : (String) temp;
        return language;
    }

    public static String asString(IHandle handle, String text) {
        String language = getLanguage(handle);
        if (Application.LangageDefault.equals(language))
            return text;

        if (text == null || "".equals(text.trim())) {
            log.error("字符串为空");
            return "file error";
        }

        if (text.length() > 150) {
            log.error("字符串长度超过150，key:" + text);
            return text;
        }
        // 校验key
        validateKey(handle, text, language);
        // 将翻译内容返回前台
        return getValue(handle, text, language);
    }

    private static void validateKey(IHandle handle, String text, String language) {
        ISystemTable systemTable = Application.getBean("systemTable", ISystemTable.class);
        SqlQuery dsLang = new SqlQuery(handle);
        dsLang.add("select value_ from %s", systemTable.getLanguage());
        dsLang.add("where key_='%s'", Utils.safeString(text));
        dsLang.add("and lang_='%s'", language);
        dsLang.open();
        if (dsLang.eof()) {
            dsLang.append();
            dsLang.setField("key_", Utils.safeString(text));
            dsLang.setField("lang_", language);
            dsLang.setField("value_", "");
            dsLang.setField("supportAndroid_", false);
            dsLang.setField("supportIphone_", false);
            dsLang.setField("enable_", true);
            dsLang.setField("updateUser_", handle.getUserCode());
            dsLang.setField("updateDate_", TDateTime.Now());
            dsLang.setField("createUser_", handle.getUserCode());
            dsLang.setField("createDate_", TDateTime.Now());
            dsLang.post();
        }
    }

    private static String getValue(IHandle handle, String text, String language) {
        ISystemTable systemTable = Application.getBean("systemTable", ISystemTable.class);
        SqlQuery dsLang = new SqlQuery(handle);
        dsLang.add("select key_,max(value_) as value_ from %s", systemTable.getLanguage());
        dsLang.add("where key_='%s'", Utils.safeString(text));
        if ("en".equals(language)) {
            dsLang.add("and (lang_='%s')", language);
        } else {
            dsLang.add("and (lang_='%s' or lang_='en')", language);
        }
        dsLang.add("group by key_");
        dsLang.open();
        String result = dsLang.getString("value_");
        return result.length() > 0 ? result : text;
    }

    public static String get(IHandle handle, String text) {
        String language = getLanguage(handle);
        if ("cn".equals(language))
            return text;

        ISystemTable systemTable = Application.getBean("systemTable", ISystemTable.class);
        // 处理英文界面
        SqlQuery ds = new SqlQuery(handle);
        ds.add("select value_ from %s", systemTable.getLanguage());
        ds.add("where key_='%s'", Utils.safeString(text));
        if (!"en".equals(language)) {
            ds.add("and (lang_='en' or lang_='%s')", language);
            ds.add("order by value_ desc");
        } else {
            ds.add("and lang_='en'", language);
        }
        ds.open();
        if (ds.eof()) {
            ds.append();
            ds.setField("key_", text);
            ds.setField("lang_", language);
            ds.setField("value_", "");
            ds.setField("updateUser_", handle.getUserCode());
            ds.setField("updateTime_", TDateTime.Now());
            ds.setField("createUser_", handle.getUserCode());
            ds.setField("createTime_", TDateTime.Now());
            ds.post();
            return text;
        }
        String result = "";
        String en_result = ""; // 默认英文
        while (ds.fetch()) {
            if ("en".equals(ds.getString("lang_")))
                en_result = ds.getString("value_");
            else
                result = ds.getString("value_");
        }
        if (!"".equals(result)) {
            return result;
        }
        if (!"".equals(en_result)) {
            return en_result;
        }
        return text;
    }
}
