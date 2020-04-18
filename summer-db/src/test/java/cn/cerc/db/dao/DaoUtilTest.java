package cn.cerc.db.dao;

import cn.cerc.core.Record;
import cn.cerc.core.Utils;
import cn.cerc.db.core.StubHandleText;
import com.google.gson.Gson;
import org.junit.Ignore;
import org.junit.Test;

import javax.persistence.Column;
import javax.persistence.Entity;

public class DaoUtilTest {

    @Test
    public void testBuildEntity() {
        StubHandleText handle = new StubHandleText();
        String text = DaoUtil.buildEntity(handle, "t_profitday", "ProfitDay");
        System.out.println(text);
    }

    @Test
    @Ignore
    public void testCopy() {
        Record record = new Record();
        record.setField("ID_", Utils.newGuid());
        record.setField("Code_", "18100101");
        record.setField("Name_", "王五");
        record.setField("Mobile_", "1350019101");
        UserTest user = new UserTest();
        DaoUtil.copy(record, user);
        System.out.println(new Gson().toJson(user));

        record = new Record();
        record.setField("ID_", Utils.newGuid());
        record.setField("Code_", "18100101");
        record.setField("Name_", "王五");
        record.setField("Mobile_", "1350019101");
        record.setField("Web_", true);
        user = new UserTest();
        DaoUtil.copy(record, user);
        System.out.println(new Gson().toJson(user));
    }

    @Test(expected = RuntimeException.class)
    @Ignore
    public void testCopy2() {
        Record record = new Record();
        record.setField("ID_", Utils.newGuid());
        record.setField("Code_", "18100101");
        record.setField("Name_", "王五");
        UserTest user = new UserTest();
        DaoUtil.copy(record, user);
    }

    @Entity(name = "s_userinfo")
    private class UserTest {
        @Column(name = "ID_")
        public String id;
        @Column(name = "Code_")
        public String code;
        @Column(name = "Name_")
        public String name;
        @Column(name = "Mobile_")
        public String mobile;
    }
}
