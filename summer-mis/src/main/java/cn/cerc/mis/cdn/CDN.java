package cn.cerc.mis.cdn;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.Utils;
import cn.cerc.db.oss.OssConnection;
import cn.cerc.mis.SummerMIS;

public class CDN {
    private static final ClassConfig config = new ClassConfig(CDN.class, SummerMIS.ID);
    // 启用内容网络分发
    public static final String OSS_CDN_ENABLE = "oss.cdn.enable";
    //浏览器缓存版本号
    public static final String BROWSER_CACHE_VERSION = "browser.cache.version";

    @Deprecated
    public static String getSite() {
        // 获取cdn的地址
        String site = config.getString(OssConnection.oss_site, "");
        // 判断cdn是否启用
        if (!Utils.isEmpty(site) && config.getBoolean(OSS_CDN_ENABLE, false))
            site += "/resources/";
        return site;
    }

    public static String get(String file) {
        // 获取cdn的地址
        String site = config.getString(OssConnection.oss_site, "");
        // 判断cdn是否启用
        if (!Utils.isEmpty(site) && config.getBoolean(OSS_CDN_ENABLE, false))
            site += "/resources/";
        return site + file + "?v=" + config.getString(BROWSER_CACHE_VERSION, "1.0.0.0");
    }
}
