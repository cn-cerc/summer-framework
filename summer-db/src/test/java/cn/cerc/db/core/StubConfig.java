package cn.cerc.db.core;

import cn.cerc.core.IConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class StubConfig implements IConfig {
    private static final Log log = LogFactory.getLog(StubConfig.class);

    private static final String SETTINGS_FILE_NAME = System.getProperty("user.home")
            + System.getProperty("file.separator") + "summer-db.properties";

    private static Properties properties = new Properties();

    static {
        try {
            File file2 = new File(SETTINGS_FILE_NAME);
            if (file2.exists()) {
                properties.load(new FileInputStream(SETTINGS_FILE_NAME));
                log.info("read properties from " + SETTINGS_FILE_NAME);
            }
        } catch (FileNotFoundException e) {
            log.warn("The settings file '" + SETTINGS_FILE_NAME + "' does not exist.");
        } catch (IOException e) {
            log.warn("Failed to load the settings from the file: " + SETTINGS_FILE_NAME);
        }
    }

    @Override
    public String getProperty(String key, String def) {
        String result = properties.getProperty(key);
        if (result == null)
            throw new RuntimeException(String.format("请准备好配置文件 %s, 以及其中 %s 的设置", SETTINGS_FILE_NAME, key));
        return result;
    }

    @Override
    public String getProperty(String key) {
        return this.getProperty(key, null);
    }
}
