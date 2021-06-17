package cn.cerc.mis.core;

public interface IOptionReader {

    String getCorpValue(String corpNo, String optionKey, String defaultValue);
    
    String getUserValue(String userCode, String optionKey, String defaultValue);
    
}
