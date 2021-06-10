package cn.cerc.db.dao;

import java.io.Serializable;

public interface BigRecord extends Serializable {

    /**
     * 将 record 与 base 的差值，合并到self
     *
     * @param base   基础数据
     * @param record 新的数据
     */
    default void mergeValue(BigRecord baseRecord, BigRecord newRecord) {
        throw new RuntimeException("not support mergeValue");
    }

    /**
     * 针对field，求出self - oldRecord的差值，返回值不允许为空
     *
     * @param field  字段名称
     * @param record 对象数据
     * @return 返回差值
     */
    default Object getDiffValue(String field, BigRecord oldRecord) {
        return null;
    }
}
