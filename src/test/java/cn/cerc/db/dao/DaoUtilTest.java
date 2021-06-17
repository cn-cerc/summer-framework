package cn.cerc.db.dao;

import org.junit.Test;

import com.google.gson.Gson;

import cn.cerc.core.Record;
import cn.cerc.core.RecordUtils;
import cn.cerc.core.Utils;
import cn.cerc.db.core.StubSession;

public class DaoUtilTest {

    @Test
    public void testBuildEntity() {
        StubSession handle = new StubSession();
        String text = DaoUtil.buildEntity(handle, "t_profitday", "ProfitDay");
        System.out.println(text);
    }

    @Test
    public void testCopy() {
        Record record = new Record();
        record.setField("ID_", Utils.newGuid());
        record.setField("Code_", "18100101");
        record.setField("Name_", "王五");
        record.setField("Mobile_", "1350019101");
        UserTest user = record.asObject(UserTest.class);
        System.out.println(new Gson().toJson(user));

        record = new Record();
        record.setField("ID_", Utils.newGuid());
        record.setField("Code_", "18100101");
        record.setField("Name_", "王五");
        record.setField("Mobile_", "1350019101");
        record.setField("Web_", true);
        user = record.asObject(UserTest.class);
        System.out.println(new Gson().toJson(user));
    }

    @Test(expected = RuntimeException.class)
    public void testCopy2() {
        Record record = new Record();
        record.setField("ID_", Utils.newGuid());
        record.setField("Code_", "18100101");
        record.setField("Name_", "王五");
        UserTest user = new UserTest();
        RecordUtils.copyToObject(record, user);
    }

}
