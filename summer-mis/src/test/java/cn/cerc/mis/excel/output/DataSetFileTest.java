package cn.cerc.mis.excel.output;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import cn.cerc.core.DataSet;
import jxl.write.WriteException;

public class DataSetFileTest {

    @Test
    @Ignore(value = "测试文件建立，仅在本地执行")
    public void testExecute() throws WriteException, IOException {
        DataSet ds = new DataSet();
        ds.append();
        ds.setField("code", "code1");
        ds.append();
        ds.setField("code", "code2");

        String fileName = "d:\\tempfile.xls";
        new DataSetFile(ds).save(fileName);
        File file = new File(fileName);
        assertTrue("文件建立失败！", file.exists());
    }
}
