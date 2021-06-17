package cn.cerc.db.mongo;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.mongodb.BasicDBObject;

import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.StubSession;

public class MongoQueryTest_addWhereFields implements IHandle {
    private ISession session;
    private MongoQuery ds;

    @Before
    public void setUp() throws Exception {
        session = new StubSession();
        ds = new MongoQuery(this);
    }

    @Test
    @Ignore
    public void test1() {
        String sql = "select * from tmp2 where code='a001' and value=3";
        BasicDBObject filter = ds.decodeWhere(sql);
        System.out.println(filter);
        BasicDBObject sort = ds.decodeOrder(sql);
        System.out.println(sort);
    }

    @Test
    @Ignore
    public void test2() {
        String sql = "select * from tmp2 where code='a001' and value=3 order by code DESC";
        BasicDBObject filter = ds.decodeWhere(sql);
        System.out.println(filter);
        BasicDBObject sort = ds.decodeOrder(sql);
        System.out.println(sort);
    }

    @Test
    @Ignore
    public void test3() {
        String sql = "select * from tmp2 where code='a001' and value=3 order by code";
        BasicDBObject filter = ds.decodeWhere(sql);
        System.out.println(filter);
        BasicDBObject sort = ds.decodeOrder(sql);
        System.out.println(sort);
    }

    @Test
    @Ignore
    public void test4() {
        String sql = "select * from tmp2 where code='a001' and value=3 order by code,value DESC";
        BasicDBObject filter = ds.decodeWhere(sql);
        System.out.println(filter);
        BasicDBObject sort = ds.decodeOrder(sql);
        System.out.println(sort);
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
