package cn.cerc.mis.language;

import java.util.Map;

public interface ILanguageReader {
    /**
     * 取得指定语言的全部对照记录，并存入到items
     */
    public int loadDictionary(Map<String, String> items, String langId);

    /**
     * 取得指定key对应的文字，若字典库不存在则写入
     */
    String getOrSet(String langId, String key);

}
