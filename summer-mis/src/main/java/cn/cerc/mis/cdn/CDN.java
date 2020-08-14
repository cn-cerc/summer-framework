package cn.cerc.mis.cdn;

import cn.cerc.core.Utils;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.db.oss.OssConnection;

public class CDN {

    public static String getSite() {
        String site = "";
        // 判断cdn是否启用
        boolean enable = "true".equals(ServerConfig.getInstance().getProperty(OssConnection.oss_cdn_enable));
        if (!enable) {
            return site;
        }
        // 获取cdn的地址
        site = ServerConfig.getInstance().getProperty(OssConnection.oss_site);
        if (Utils.isEmpty(site)) {
            site = "";
        } else {
            site += "/resources/";
        }
        return site;
    }

    public static String get(String file) {
        return getSite() + file;
    }

}
