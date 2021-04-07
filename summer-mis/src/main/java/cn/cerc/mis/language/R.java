package cn.cerc.mis.language;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.LanguageResource;
import cn.cerc.core.TDateTime;
import cn.cerc.core.Utils;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.IAppLanguage;
import cn.cerc.mis.core.ISystemTable;

//TODO 此对象需要做更进一步抽象处理
public class R {
    private static final Logger log = LoggerFactory.getLogger(R.class);

    public static String getLanguageId(IHandle handle) {
        if (handle == null) {
            log.warn("handle is null.");
            return null;
        }
        Object temp = handle.getSession().getProperty(Application.deviceLanguage);
        if (temp == null || "".equals(temp)) {
            log.debug("handle language is null");
            Object request = handle.getSession().getProperty("request");
            if (request != null) {
                log.debug(request.getClass().getName());
                if (request instanceof HttpServletRequest) {
                    HttpServletRequest req = (HttpServletRequest) request;
                    temp = req.getSession().getAttribute(Application.deviceLanguage);
                    log.debug("session language value " + temp);
                }
            }
        }

        String language = temp == null ? Application.getLanguage() : (String) temp;

        try {
            IAppLanguage obj = Application.getBeanDefault(IAppLanguage.class, handle.getSession());
            language = obj.getLanguageId(language);
        } catch (Exception e) {
            language = Application.App_Language;
        }
        return language;
    }

    public static String asString(IHandle handle, String text) {
        String language = getLanguageId(handle);
        if (Application.App_Language.equals(LanguageResource.LANGUAGE_CN)) {
            return text;
        }
        if (text == null || "".equals(text.trim())) {
            log.error("text is empty");
            return "file error";
        }

        if (text.length() > 150) {
            log.error("The key length exceeds 150: {}", text);
            return text;
        }
        // 校验key
        validateKey(handle, text, language);
        // 将翻译内容返回前台
        return getValue(handle, text, language);
    }

    private static void validateKey(IHandle handle, String text, String language) {
        ISystemTable systemTable = Application.getBeanDefault(ISystemTable.class, null);
        SqlQuery dsLang = new SqlQuery(handle);
        dsLang.add("select Value_ from %s", systemTable.getLanguage());
        dsLang.add("where Key_='%s'", Utils.safeString(text));
        dsLang.add("and Lang_='%s'", language);
        dsLang.open();
        if (dsLang.eof()) {
            dsLang.append();
            dsLang.setField("Key_", Utils.safeString(text));
            dsLang.setField("Lang_", language);
            dsLang.setField("Value_", "");
            dsLang.setField("SupportAndroid_", false);
            dsLang.setField("SupportIphone_", false);
            dsLang.setField("Enable_", true);
            dsLang.setField("UpdateUser_", handle.getUserCode());
            dsLang.setField("UpdateDate_", TDateTime.now());
            dsLang.setField("CreateUser_", handle.getUserCode());
            dsLang.setField("CreateDate_", TDateTime.now());
            dsLang.post();
        }
    }

    private static String getValue(IHandle handle, String text, String language) {
        ISystemTable systemTable = Application.getBeanDefault(ISystemTable.class, null);
        SqlQuery dsLang = new SqlQuery(handle);
        dsLang.add("select Key_,max(Value_) as Value_ from %s", systemTable.getLanguage());
        dsLang.add("where Key_='%s'", Utils.safeString(text));
        dsLang.add("and (Lang_='%s')", language);
        // FIXME: 2019/12/7 此处应该取反了，未来得及翻译的语言应该直接显示中文
        // if (Language.en_US.equals(language)) {
        // dsLang.add("and (Lang_='%s')", language);
        // } else {
        // dsLang.add("and (Lang_='%s' or Lang_='en')", language);
        // }
        dsLang.add("group by Key_");
        dsLang.open();
        String result = dsLang.getString("Value_");
        return result.length() > 0 ? result : text;
    }

//    public static String get(IHandle handle, String text) {
//        String language = getLanguageId(handle);
//        if (Language.zh_CN.equals(language)) {
//            return text;
//        }
//
//        ISystemTable systemTable = Application.getBeanDefault(ISystemTable.class, null);
//        // 处理英文界面
//        SqlQuery ds = new SqlQuery(handle);
//        ds.add("select Value_ from %s", systemTable.getLanguage());
//        ds.add("where Key_='%s'", Utils.safeString(text));
//        if (!Language.en_US.equals(language)) {
//            ds.add("and (Lang_='en' or Lang_='%s')", language);
//            ds.add("order by Value_ desc");
//        } else {
//            ds.add("and Lang_='en'", language);
//        }
//        ds.open();
//        if (ds.eof()) {
//            ds.append();
//            ds.setField("Key_", text);
//            ds.setField("Lang_", language);
//            ds.setField("Value_", "");
//            ds.setField("UpdateUser_", handle.getUserCode());
//            ds.setField("UpdateTime_", TDateTime.now());
//            ds.setField("CreateUser_", handle.getUserCode());
//            ds.setField("CreateTime_", TDateTime.now());
//            ds.post();
//            return text;
//        }
//        String result = "";
//        String en_result = ""; // 默认英文
//        while (ds.fetch()) {
//            if (Language.en_US.equals(ds.getString("Lang_"))) {
//                en_result = ds.getString("Value_");
//            } else {
//                result = ds.getString("Value_");
//            }
//        }
//        if (!"".equals(result)) {
//            return result;
//        }
//        if (!"".equals(en_result)) {
//            return en_result;
//        }
//        return text;
//    }
}
