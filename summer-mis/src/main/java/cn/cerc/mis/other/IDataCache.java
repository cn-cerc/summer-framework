package cn.cerc.mis.other;

@Deprecated
public interface IDataCache extends IDataList {

    boolean exist(String key);

    @Override
    default boolean exists(String key) {
        return exist(key);
    }
}
