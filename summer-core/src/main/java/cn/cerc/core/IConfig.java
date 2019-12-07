package cn.cerc.core;

public interface IConfig {
	public String getProperty(String key, String def);

	public String getProperty(String key);
	
	// default public String getProperty(String key) {
	// return this.getProperty(key, null);
	// }
}
