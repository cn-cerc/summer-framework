package cn.cerc.db.nas;

import cn.cerc.db.core.StubHandle;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class NasQueryTest {

    private NasQuery ds;
    private StubHandle handle;

    @Before
    public void setUp() {
        handle = new StubHandle();
        ds = new NasQuery(handle);
    }

    /**
     * 保存文件/覆盖文件
     * 
     * @Description
     * @author rick_zhou
     */
    @Test
    public void saveFile() {
        /*
         * ds.add("select test.txt from %s", appdb.get(handle,appdb.NAS_FOLDER));
         */
        ds.add("select test.txt from %s", "D://testFolder1/testFolder2");
        ds.setNasMode(NasModel.create);
        ds.open();
        ds.append();
        ds.setField("key", "一大串字符串................................................");
        ds.save();
        // update
        ds.setField("key", "一大串字符串2................................................");
        ds.save();
        ds.setField("key2", "一大串字符串3................................................");
        ds.save();
    }

    /**
     * 获取文件内容
     * 
     * @Description
     * @author rick_zhou
     */
    @Test
    public void findFile() {
        /*
         * ds.add("select test.txt from %s", appdb.get(handle,appdb.NAS_FOLDER));
         */
        ds.add("select test.txt from %s", "D://testFolder1/testFolder2");
        ds.setNasMode(NasModel.readWrite);
        ds.open();
        log.info("获取到的文件内容为:\n" + ds.getField("key"));
        log.info("获取到的文件内容为:\n" + ds.getField("key2"));
    }

    /**
     * 删除文件
     * 
     * @Description
     * @author rick_zhou
     */
    @Test
    public void deleteFile() {
        /*
         * ds.add("select test.txt from %s", appdb.get(handle,appdb.NAS_FOLDER));
         */
        ds.add("select test.txt from %s", "D://testFolder1/testFolder2");
        ds.open();
        ds.delete();
    }

}
