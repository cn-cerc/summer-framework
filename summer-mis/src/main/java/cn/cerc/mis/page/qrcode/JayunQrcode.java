package cn.cerc.mis.page.qrcode;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import cn.cerc.core.MD5;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.security.sapi.JayunAPI;

public class JayunQrcode {

    private static final Logger log = LoggerFactory.getLogger(JayunQrcode.class);

    private static final String ACTION_LOGIN = "login";
    private static final String ACTION_BIND = "bind";

    private HttpServletRequest request;
    private HttpServletResponse response;

    public JayunQrcode(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public void execute(JayunEasyLogin sender) {
        String action = request.getParameter("action");
        String appKey = request.getParameter("appKey");
        long timestamp = Long.parseLong(request.getParameter("timestamp"));
        String reqSign = request.getParameter("sign");
        String appSecret = ServerConfig.getInstance().getProperty(JayunAPI.JAYUN_APP_SECRET);
        String sign = getSign(appKey, appSecret, action, timestamp);

        if (!sign.equals(reqSign)) {
            JayunMessage msg = new JayunMessage(false, "签名失败");
            echo(msg);
            return;
        }

        if (ACTION_LOGIN.equals(action)) {
            login(sender);
        } else if (ACTION_BIND.equals(action)) {
            bind(sender);
        } else {
            scan();
        }
    }

    private void scan() {
        String sessionId = request.getParameter("sessionId");
        boolean result = "true".equals(request.getParameter("result"));
        String message = request.getParameter("message");
        JayunMessage obj = new JayunMessage(result, message);
        WebSocket.getWebSocketSet().get(sessionId);
        WebSocket ws = WebSocket.getWebSocketSet().get(sessionId);
        if (ws != null) {
            echo(obj);
        } else {
            String socket_url = new SocketTool().getSocketUrl(request);
            log.error("扫描回调地址 {}", socket_url);
            echo(new JayunMessage(false, "没有找到相对应的Socket客户端"));
        }
    }

    private void login(JayunEasyLogin sender) {
        String sessionId = request.getParameter("sessionId");
        WebSocket ws = WebSocket.getWebSocketSet().get(sessionId);
        if (ws != null) {
            JayunMessage obj = sender.getLoginToken();
            if (obj.isResult()) {
                WebSocket.getWebSocketSet().get(sessionId).sendMessage(obj.toString());
            }
            echo(obj);
        } else {
            String socket_url = new SocketTool().getSocketUrl(request);
            log.error("登录回调地址 {}", socket_url);
            echo(new JayunMessage(false, "没有找到相对应的Socket客户端"));
        }
    }

    private void bind(JayunEasyLogin sender) {
        boolean result = "true".equals(request.getParameter("result"));
        String message = request.getParameter("message");
        JayunMessage obj = new JayunMessage(result, message);
        obj.setUrl(sender.getNotifyUrl());

        String sessionId = request.getParameter("sessionId");
        WebSocket ws = WebSocket.getWebSocketSet().get(sessionId);
        if (ws != null) {
            WebSocket.getWebSocketSet().get(sessionId).sendMessage(obj.toString());
        } else {
            String socket_url = new SocketTool().getSocketUrl(request);
            log.error("绑定回调地址 {}", socket_url);
            echo(new JayunMessage(false, "没有找到相对应的Socket客户端"));
        }
    }

    public static String getSign(String appKey, String appSecret, String action, long timestamp) {
        Map<String, Object> tmp = new TreeMap<>();
        tmp.put("action", action);
        tmp.put("appKey", appKey);
        tmp.put("appSecret", appSecret);
        tmp.put("timestamp", timestamp);
        // 比较两者的签名
        Gson gson = new Gson();
        return MD5.get(gson.toJson(tmp)).toUpperCase();
    }

    private void echo(JayunMessage msg) {
        PrintWriter writer;
        try {
            writer = response.getWriter();
            writer.print(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
