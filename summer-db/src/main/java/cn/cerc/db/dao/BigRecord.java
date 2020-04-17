package cn.cerc.db.dao;

import java.io.Serializable;

public interface BigRecord extends Serializable {

    /**
     * 将 record 与 base 的差值，合并到self
     *
     * @param base   基础数据
     * @param record 新的数据
     * @return 合并信息
     */
    void merge(BigRecord base, BigRecord record);

    /**
     * 针对field，求出self - record的差值
     *
     * @param field  字段名称
     * @param record 对象数据
     * @return 返回差值
     */
    Object getDiffValue(String field, BigRecord record);
}
