package cn.cerc.db.mongo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

import cn.cerc.core.IConfig;
import cn.cerc.core.IConnection;
import cn.cerc.db.core.ServerConfig;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MongoConnection implements IConnection, AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(MongoConnection.class);

    public static final String mgdb_dbname = "mgdb.dbname";
    public static final String mgdb_username = "mgdb.username";
    public static final String mgdb_password = "mgdb.password";
    public static final String mgdb_site = "mgdb.ipandport";
    public static final String mgdb_enablerep = "mgdb.enablerep";
    public static final String mgdb_replicaset = "mgdb.replicaset";
    public static final String mgdb_maxpoolsize = "mgdb.maxpoolsize";
    public static final String sessionId = "mongoSession";

    private static MongoClient pool;
    private static String dbname;
    private MongoDatabase database;
    private IConfig config;

    public MongoConnection() {
        config = ServerConfig.getInstance();
    }

    @Override
    public MongoDatabase getClient() {
        if (database != null) {
            return database;
        }

        if (MongoConnection.pool == null) {
            dbname = config.getProperty(MongoConnection.mgdb_dbname);
            StringBuffer sb = new StringBuffer();
            sb.append("mongodb://");
            // userName
            sb.append(config.getProperty(MongoConnection.mgdb_username));
            // password
            sb.append(":").append(config.getProperty(MongoConnection.mgdb_password));
            // ip
            sb.append("@").append(config.getProperty(MongoConnection.mgdb_site));
            // database
            sb.append("/").append(config.getProperty(MongoConnection.mgdb_dbname));

            if ("true".equals(config.getProperty(MongoConnection.mgdb_enablerep))) {
                // replacaset
                sb.append("?").append("replicaSet=").append(config.getProperty(MongoConnection.mgdb_replicaset));
                // poolsize
                sb.append("&").append("maxPoolSize=").append(config.getProperty(MongoConnection.mgdb_maxpoolsize));
                sb.append("&").append("connectTimeoutMS=").append("3000");
                sb.append("&").append("serverSelectionTimeoutMS=").append("3000");
                log.info("Connect to the MongoDB sharded cluster:" + sb.toString());
            }
            MongoClientURI connectionString = new MongoClientURI(sb.toString());
            pool = new MongoClient(connectionString);
        }
        database = pool.getDatabase(dbname);
        return database;
    }

    @Override
    public void close() {
        if (database != null) {
            database = null;
        }
    }

    @Override
    public String getClientId() {
        return sessionId;
    }

    public IConfig getConfig() {
        return config;
    }

    @Override
    public void setConfig(IConfig config) {
        this.config = config;
    }

}