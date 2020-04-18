package cn.cerc.db.oss;

import cn.cerc.db.core.StubHandleText;
import org.junit.BeforeClass;
import org.junit.Test;

public class OssQuerySend {
    private static OssQuery ds;
    private static StubHandleText handle;

    @BeforeClass
    public static void setUp() {
        handle = new StubHandleText();
        ds = new OssQuery(handle);
    }

    /**
     * 保存文件/覆盖文件
     */
    @Test
    public void saveFile() {
        ds.setOssMode(OssMode.create);
        ds.add("select * from %s", "id_00001.txt");
        ds.setOssMode(OssMode.readWrite);
        ds.open();
        ds.append();
        ds.setField("num", ds.getInt("num") + 1);
        ds.save();
    }

}
