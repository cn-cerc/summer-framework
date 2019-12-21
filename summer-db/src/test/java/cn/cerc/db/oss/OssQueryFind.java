package cn.cerc.db.oss;

import org.junit.BeforeClass;
import org.junit.Test;

import cn.cerc.db.core.StubHandle;

public class OssQueryFind {
    private static OssQuery ds;
    private static StubHandle handle;

    @BeforeClass
    public static void setUp() {
        handle = new StubHandle();
        ds = new OssQuery(handle);
    }

    /**
     * 查询文件
     *
     * @Description
     * @author rick_zhou
     */
    @Test
    public void queryFile() {
        ds.setOssMode(OssMode.readWrite);
        ds.add("select * from %s", "id_00001.txt");
        ds.open();
    }

}
