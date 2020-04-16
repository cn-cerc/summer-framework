package cn.cerc.db.core;

import cn.cerc.core.IConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

@Slf4j
public enum LocalConfig implements IConfig {

    INSTANCE;

    private static final String confFile = System.getProperty("user.home") + System.getProperty("file.separator")
            + "summer-application.properties";

    private static Properties properties = new Properties();

    static {
        try {
            properties.clear();
            File file = new File(confFile);
            if (file.exists()) {
                properties.load(new FileInputStream(confFile));
                log.info("read properties from : {}", confFile);
            } else {
                log.warn("suggested use properties: {}", confFile);
            }
        } catch (FileNotFoundException e) {
            log.error("The settings file does not exist: {}'", confFile);
        } catch (IOException e) {
            log.error("Failed to load properties from the file: {}", confFile);
        }
    }

    @Override
    public String getProperty(String key, String def) {
        String result = null;
        if (properties != null) {
            result = properties.getProperty(key);
        }
        return result != null ? result : def;
    }

    @Override
    public String getProperty(String key) {
        return getProperty(key, null);
    }

    public static void main(String[] args) {
        LocalConfig config1 = LocalConfig.INSTANCE;
        System.out.println(config1.getProperty("rds.site"));
    }

}
