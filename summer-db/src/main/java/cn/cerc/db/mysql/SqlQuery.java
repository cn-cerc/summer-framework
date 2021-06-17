package cn.cerc.db.mysql;

import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.MysqlQuery;

@SuppressWarnings("serial")
public class SqlQuery extends MysqlQuery {

    public SqlQuery() {
        super();
    }

    public SqlQuery(IHandle handle) {
        super(handle);
    }

}
