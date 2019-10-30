package cn.cerc.db.mysql;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SlaveMysqlConnection extends SqlConnection {
    // IHandle中识别码
    public static final String sessionId = "slaveSqlSession";
    // Propertys slave 中识别码
    public static final String rds_slave_site = "rds.slave.site";
    public static final String rds_slave_database = "rds.slave.database";
    public static final String rds_slave_username = "rds.slave.username";
    public static final String rds_slave_password = "rds.slave.password";

    public static String slaveDataSource = "slaveDataSource";

    @Override
    public String getClientId() {
        return sessionId;
    }

    @Override
    protected String getConnectUrl() {
        String host = config.getProperty(SlaveMysqlConnection.rds_slave_site, "127.0.0.1:3306");
        String db = config.getProperty(SlaveMysqlConnection.rds_slave_database, "appdb");
        user = config.getProperty(SlaveMysqlConnection.rds_slave_username, "appdb_user");
        pwd = config.getProperty(SlaveMysqlConnection.rds_slave_password, "appdb_password");
        if (host == null || user == null || pwd == null || db == null)
            throw new RuntimeException("RDS配置为空，无法连接主机！");

        return String.format("jdbc:mysql://%s/%s?useSSL=false", host, db);
    }
}
