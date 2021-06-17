package cn.cerc.mis.language;

import java.util.Map;

import cn.cerc.db.core.IHandle;

public interface ILanguageReader {
    /**
     * 取得指定语言的全部对照记录，并存入到items
     */
    public int loadDictionary(IHandle handle, Map<String, String> items, String langId);

    /**
     * 取得指定key对应的文字，若字典库不存在则写入
     */
    String getOrSet(IHandle handle, String langId, String key);

}
