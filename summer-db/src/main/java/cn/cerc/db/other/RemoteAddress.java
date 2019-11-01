package cn.cerc.db.other;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class RemoteAddress {

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
