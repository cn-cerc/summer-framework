package cn.cerc.db.oracle;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.cerc.core.IConfig;
import cn.cerc.core.IConnection;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OracleConnection implements IConnection {

    @Override
    public Object getClient() {
        return null;
    }

    @Override
    public String getClientId() {
        return "oracleSession";
    }

    @Override
    public void setConfig(IConfig config) {

    }

}
