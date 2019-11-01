package cn.cerc.db.dao;

import java.util.concurrent.atomic.AtomicBoolean;

public interface BigControl {
    AtomicBoolean getActive();

    void registerTable(BigTable<?> table);
}
