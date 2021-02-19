package cn.cerc.mis.sms;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.IHandle;

/**
 * 获取操作的ip
 * 
 * @author 欧阳军
 *
 */
public class RemoteIP {
    private static final Logger log = LoggerFactory.getLogger(RemoteIP.class);

    public static String get(IHandle handle) {
        HttpServletRequest request = (HttpServletRequest) handle.getProperty("request");
        if (request == null) {
            log.warn("handle 不存在 request 对象");
            return "127.0.0.1";
        }

        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}
