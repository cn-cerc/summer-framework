package cn.cerc.mis.cdn;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.Utils;
import cn.cerc.db.oss.OssConnection;
import cn.cerc.mvc.SummerMVC;

public class CDN {
    private static final ClassConfig config = new ClassConfig(CDN.class, SummerMVC.ID);

    public static String getSite() {
        // 判断cdn是否启用
        if (!config.getBoolean(OssConnection.oss_cdn_enable, false)) {
            return "";
        }

        // 获取cdn的地址
        String site = config.getString(OssConnection.oss_site, null);
        if (Utils.isEmpty(site)) {
            return "";
        }

        return site + "/resources/";
    }

    public static String get(String file) {
        return getSite() + file + "?v=" + config.getString("browser.cache.version", "1.0.0.0");
    }
}
