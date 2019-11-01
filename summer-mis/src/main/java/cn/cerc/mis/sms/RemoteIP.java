package cn.cerc.mis.sms;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.IHandle;
import cn.cerc.mis.task.AbstractTask;
import lombok.extern.slf4j.Slf4j;

/**
 * 获取操作的ip
 */
@Slf4j
public class RemoteIP {

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
