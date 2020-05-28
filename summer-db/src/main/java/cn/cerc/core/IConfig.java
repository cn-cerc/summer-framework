package cn.cerc.core;

public interface IConfig {
    String getProperty(String key, String def);

    String getProperty(String key);

    // default public String getProperty(String key) {
    // return this.getProperty(key, null);
    // }
}
