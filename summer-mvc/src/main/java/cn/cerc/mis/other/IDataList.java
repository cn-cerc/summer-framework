package cn.cerc.mis.other;

@Deprecated
/**
 * FIXME 建议使用 IDataCache
 */
public interface IDataList {

    void clear();

    boolean exists(String key);
}
