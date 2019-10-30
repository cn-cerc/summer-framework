package cn.cerc.db.dao;

import java.io.Serializable;

public abstract interface BigRecord extends Serializable {

    /**
     * 将 record 与 base 的差值，合并到self
     * 
     * @param base
     * @param record
     */
    void merge(BigRecord base, BigRecord record);

    /**
     * 针对field，求出self - record的差值
     * 
     * @param field
     * @param record
     * @return 返回差值
     */
    Object getDiffValue(String field, BigRecord record);
}
