package cn.cerc.mis.page.qrcode;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketTool {

    private static final Logger log = LoggerFactory.getLogger(SocketTool.class);

    // FIXME 改为参数传入
    private static List<String> items = new ArrayList<>();
    static {
        items.add("diteng");
        items.add("knowall");
    }

    public String getDomain(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        String domain = url.delete(url.length() - request.getRequestURI().length(), url.length()).toString();
        if (domain.indexOf("http://") != -1) {
            domain = domain.replace("http://", "");
        } else if (domain.indexOf("https://") != -1) {
            domain = domain.replace("https://", "");
        }
        return domain;
    }

    public String getSocketUrl(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        String domain = url.delete(url.length() - request.getRequestURI().length(), url.length()).toString();

        log.info("domain: {}", domain);

        String socketUrl = domain;
        if (domain.indexOf("http://") != -1) {
            socketUrl = domain.replace("http", "ws");

            // 遇到指定域名 websocket 开启 ssl
            for (String item : items) {
                if (socketUrl.indexOf(item) != -1) {
                    socketUrl = socketUrl.replace("ws", "wss");
                    continue;
                }
            }

        } else if (domain.indexOf("https://") != -1) {
            socketUrl = domain.replace("https", "wss");
        }
        return socketUrl += "/websocket";
    }

}
