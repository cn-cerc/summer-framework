package cn.cerc.db.mysql;

import java.io.Serializable;

import cn.cerc.core.DataQuery;

/**
 * 单次数据请求，超过最大笔数限制
 * 
 * @author 张弓
 *
 */
public class BigdataException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = -7618888023082541077L;

    public static final int MAX_RECORDS = 50000;

    public BigdataException(DataQuery dataSet, int rows) {
        super(String.format("本次请求的记录数超出了系统最大笔数为  %d 的限制！", MAX_RECORDS));
    }

    public static void check(DataQuery dataset, int rows) {
        if (rows > (MAX_RECORDS + 1)) 
            throw new BigdataException(dataset, rows);
    }

}
