package cn.cerc.mis.core;

import cn.cerc.db.core.ServerConfig;

public enum HTMLResource {

    INSTANCE;

    public static String getVersion() {
        ServerConfig config = ServerConfig.getInstance();
        return config.getProperty("browser.cache.version", "1.0.0.0");
    }

}
