package cn.cerc.db.mongo;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.mongodb.BasicDBObject;

import cn.cerc.db.core.StubHandle;

public class MongoQueryTest_addWhereFields {
    private StubHandle handle;
    private MongoQuery ds;

    @Before
    public void setUp() throws Exception {
        handle = new StubHandle();
        ds = new MongoQuery(handle);
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

}
