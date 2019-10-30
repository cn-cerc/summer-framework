package cn.cerc.db.other;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteAddress {
    private static final Logger log = LoggerFactory.getLogger(RemoteAddress.class);

    // 获取最终访问者的ip地址
    public static String get(HttpServletRequest request) {
        if (request == null) {
            log.warn("不存在 request 对象");
            return "127.0.0.1";
        }

        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 获取最右边的ip地址
        String[] args = ip.split(",");
        return args[args.length - 1];
    }

}
