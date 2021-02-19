package cn.cerc.mis.language;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.IHandle;
import cn.cerc.core.Record;
import cn.cerc.mis.core.LocalService;

public class ResourceBuffer {
    private static final Logger log = LoggerFactory.getLogger(ResourceBuffer.class);
    private String lang;
    private static Map<String, String> items = new HashMap<>();

    public ResourceBuffer(String lang) {
        this.lang = lang;
    }

    public String get(IHandle handle, String text) {
        if (items.size() == 0) {
            LocalService svr = new LocalService(handle, "SvrLanguage.downloadAll");
            Record headIn = svr.getDataIn().getHead();
            headIn.setField("lang_", lang);
            if (!svr.exec()) {
                log.error(svr.getMessage());
                return text;
            }
            for (Record item : svr.getDataOut()) {
                items.put(item.getString("key_"), item.getString("value_"));
            }
            if (items.size() == 0) {
                log.error("没有找到相应的语言字典数据！！！");
            }
        }
        if (items.containsKey(text)) {
            return items.get(text);
        }

        String result = getValue(handle, text);
        items.put(text, result);
        return result;
    }

    private String getValue(IHandle handle, String text) {
        LocalService svr = new LocalService(handle, "SvrLanguage.download");
        Record headIn = svr.getDataIn().getHead();
        headIn.setField("key_", text);
        headIn.setField("lang_", lang);
        if (!svr.exec()) {
            log.error(svr.getMessage());
            return text;
        }

        return svr.getDataOut().getHead().getString("value");
    }

    public void clear() {
        items.clear();
    }
}
