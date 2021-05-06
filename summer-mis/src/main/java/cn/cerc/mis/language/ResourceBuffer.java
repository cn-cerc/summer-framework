package cn.cerc.mis.language;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.db.core.IHandle;
import cn.cerc.mis.core.Application;

//TODO 此对象需要做更进一步抽象处理
public class ResourceBuffer {
    private static final Logger log = LoggerFactory.getLogger(ResourceBuffer.class);
    private static Map<String, String> items = new HashMap<>();
    private String lang;

    public ResourceBuffer(String lang) {
        this.lang = lang;
    }

    public String get(IHandle handle, String text) {
        if (items.size() == 0) {
            ILanguageReader reader = Application.getBean(handle, ILanguageReader.class);
            if (reader.loadDictionary(handle, items, lang) == 0)
                log.error("dictionary data can not be found");
        }
        if (items.containsKey(text)) {
            return items.get(text);
        }

        ILanguageReader reader = Application.getBean(handle, ILanguageReader.class);
        String result =  reader.getOrSet(handle, lang, text);
        items.put(text, result);
        return result;
    }

    public void clear() {
        items.clear();
    }
}
