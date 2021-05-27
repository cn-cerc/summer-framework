package cn.cerc.db.oss;

import org.junit.Before;
import org.junit.Test;

import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.StubSession;

public class OssQueryTest_send implements IHandle {
    private OssQuery ds;
    private ISession session;

    @Before
    public void setUp() {
        session = new StubSession();
        ds = new OssQuery(this);
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

    @Override
    public ISession getSession() {
        return session;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
    }
}
