package cn.cerc.db.oss;

import org.junit.Before;
import org.junit.Test;

import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.StubSession;

public class OssQueryTest_find implements IHandle {
    private OssQuery ds;
    private ISession session;

    @Before
    public void setUp() {
        session = new StubSession();
        ds = new OssQuery(this);
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

    @Override
    public ISession getSession() {
        return session;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
    }
}
