package cn.cerc.db.queue;

import cn.cerc.core.ClassResource;
import cn.cerc.core.Record;
import cn.cerc.db.SummerDB;
import cn.cerc.db.core.NosqlOperator;

public class QueueOperator implements NosqlOperator {
    private static final ClassResource res = new ClassResource(QueueOperator.class, SummerDB.ID);

    @Override
    public boolean insert(Record record) {
        throw new RuntimeException(res.getString(1, "消息队列服务，不支持插入操作"));
    }

    @Override
    public boolean update(Record record) {
        throw new RuntimeException(res.getString(2, "消息队列服务，不支持修改操作"));
    }

    @Override
    public boolean delete(Record record) {
        throw new RuntimeException(res.getString(3, "消息队列服务，不支持删除操作"));
    }
}
