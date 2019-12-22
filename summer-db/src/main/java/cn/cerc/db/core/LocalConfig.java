package cn.cerc.db.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import cn.cerc.core.IConfig;
import cn.cerc.core.Utils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalConfig implements IConfig {

    private static final String SUMMER_APPLICATION = System.getProperty("user.home") + System.getProperty("file.separator") + "summer-application.properties";

    private static final String SUMMER_REPLICA = System.getProperty("user.home") + System.getProperty("file.separator") + "summer-replcia.properties";

    private static Properties properties = new Properties();
    private static LocalConfig instance;

    private String confFile;

    public synchronized static LocalConfig getInstance() {
        if (instance == null) {
            new LocalConfig();
        }
        return instance;
    }

    private LocalConfig() {
        if (instance != null) {
            log.error("LocalConfig instance is not null");
        }
        instance = this;
        refresh();
    }

    private void refresh() {
        try {
            properties.clear();
            File file = new File(this.getConfFile());
            if (file.exists()) {
                properties.load(new FileInputStream(confFile));
                log.info("read properties from : {}", confFile);
            } else {
                log.warn("suggested use properties: {}", confFile);
            }
        } catch (FileNotFoundException e) {
            log.error("The settings file does not exist: {}'", confFile);
        } catch (IOException e) {
            log.error("Failed to load the settings from the file: {}", confFile);
        }
    }

    public static void main(String[] args) {
        LocalConfig config1 = LocalConfig.getInstance();
        System.out.println(config1.getProperty("mssql.site"));

        config1.setConfFile(SUMMER_REPLICA);
        System.out.println(config1.getProperty("mssql.site"));

        LocalConfig config2 = LocalConfig.getInstance();
        System.out.println("con2: " + config2.getProperty("mssql.site"));
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

    public String getConfFile() {
        if (this.confFile == null) {
            this.confFile = LocalConfig.SUMMER_APPLICATION;
        }
        return confFile;
    }

    public LocalConfig setConfFile(String confFile) {
        if (Utils.isEmpty(confFile)) {
            throw new RuntimeException("properties的文件路径不允许为空");
        }
        this.confFile = confFile;
        refresh();
        return this;
    }

}
