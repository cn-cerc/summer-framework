package cn.cerc.mis.other;

public interface IDataCache {

    void clear();

    boolean exist(String key);
}
