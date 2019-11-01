package cn.cerc.db;

import cn.cerc.core.DataSet;

/**
 * 专用于建立测试类数据
 *
 * @author 张弓
 */
public class SampleData {

    public static DataSet getDataSet() {
        DataSet ds = new DataSet();
        for (int i = 1; i < 10; i++) {
            ds.append();
            ds.setField("code", "code" + i);
            ds.setField("name", "name" + i);
            ds.setField("num", i);
        }
        return ds;
    }
}
