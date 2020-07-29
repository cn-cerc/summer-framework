package cn.cerc.mis.cdn;

import cn.cerc.core.Utils;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.db.oss.OssConnection;

public class CDN {

    public static String get(String file) {
        // 判断cdn是否启用
        boolean enable = "true".equals(ServerConfig.getInstance().getProperty(OssConnection.oss_cdn_enable));
        if (!enable) {
            return file;
        }

        // 获取cdn的地址
        String site = ServerConfig.getInstance().getProperty(OssConnection.oss_site);
        if (Utils.isEmpty(site)) {
            return file;
        }

        site += "/resources/";
        site += file;
        return site;
    }

}
