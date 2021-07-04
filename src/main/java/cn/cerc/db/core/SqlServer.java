package cn.cerc.db.core;

import cn.cerc.core.IConnection;

public interface SqlServer extends IConnection {

    boolean execute(String sql);
    
    SqlOperator getDefaultOperator(IHandle handle);

}
