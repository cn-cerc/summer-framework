package cn.cerc.db.oss;

import cn.cerc.db.core.StubHandleText;
import org.junit.BeforeClass;
import org.junit.Test;

public class OssQueryFind {
    private static OssQuery ds;
    private static StubHandleText handle;

    @BeforeClass
    public static void setUp() {
        handle = new StubHandleText();
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
