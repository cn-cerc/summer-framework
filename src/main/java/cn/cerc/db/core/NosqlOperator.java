package cn.cerc.db.core;

import cn.cerc.core.Record;

public interface NosqlOperator {

    boolean insert(Record record);

    boolean update(Record record);

    boolean delete(Record record);

}
