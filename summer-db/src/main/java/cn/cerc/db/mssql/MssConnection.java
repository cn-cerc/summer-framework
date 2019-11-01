package cn.cerc.db.mssql;

import cn.cerc.core.IConfig;
import cn.cerc.core.IConnection;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MssConnection implements IConnection {

    @Override
    public Object getClient() {
        return null;
    }

    @Override
    public String getClientId() {
        return "mssqlSession";
    }

    @Override
    public void setConfig(IConfig config) {

    }

}
