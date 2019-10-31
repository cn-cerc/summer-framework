package cn.cerc.security.sapi;

import cn.cerc.db.core.Curl;
import cn.cerc.db.core.ServerConfig;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class JayunAPI {

    public static final String jayunHost = "https://www.jayun.site";
    public static final String JAYUN_APP_KEY = "jayun.appKey";
    public static final String JAYUN_APP_SECRET = "jayun.appSecret";
    public static final String JAYUN_STOP = "jayun.stop";
    private static final Logger log = Logger.getLogger(JayunAPI.class);
    private static boolean isServerRegister = false;

    private Object data;
    private String message;
    private boolean result;

    // 停止调用聚安
    private boolean stop = false;

    private HttpServletRequest request;
    private String remoteIP;
    private Curl curl;

    public JayunAPI(HttpServletRequest request) {
        this.request = request;
        this.remoteIP = getRemoteAddr();
        this.curl = new Curl();

        ServerConfig config = ServerConfig.getInstance();
        String appKey = config.getProperty(JAYUN_APP_KEY);
        if (appKey != null) {
            this.put("appKey", appKey);
        }

        String jayunStop = config.getProperty(JAYUN_STOP);
        if (jayunStop != null) {
            this.stop = "1".equals(jayunStop);
        }
    }

    public static String getHost() {
        return jayunHost;
    }

    public void post(String serviceUrl) {
        if (this.stop) {
            this.result = true;
            this.message = "聚安配置已关闭";
            return;
        }

        this.init();

        this.result = false;
        this.message = null;
        try {
            String reqUrl = String.format("%s/api/%s", jayunHost, serviceUrl);
            String result = curl.doPost(reqUrl);
            JSONObject json = JSONObject.fromObject(result);
            if (json.has("data")) {
                this.data = json.get("data");
            }
            if (json.has("result")) {
                if (json.has("message")) {
                    this.message = json.getString("message");
                }
                this.result = json.getBoolean("result");
            } else {
                this.message = result;
            }
        } catch (Exception e) {
            log.error("请求的网址不存在，或服务暂停使用中");
            this.message = e.getMessage();
            e.printStackTrace();
        }
    }

    private void init() {
        if (isServerRegister)
            return;
        try {
            ServerConfig config = ServerConfig.getInstance();
            String appKey = config.getProperty(JAYUN_APP_KEY);
            String appSecret = config.getProperty(JAYUN_APP_SECRET);
            if (appKey == null) {
                log.error("jayun.appKey 未设置，无法自动注册 ");
                return;
            }
            if (appSecret == null) {
                log.error("jayun.appSecret 未设置，无法自动注册 ");
                return;
            }
            try {
                String reqUrl = String.format("%s/api/%s", jayunHost, "server.register");
                Curl curl = new Curl();
                curl.putParameter("appKey", appKey);
                curl.putParameter("appSecret", appSecret);
                String result = curl.doPost(reqUrl);

                JSONObject json = JSONObject.fromObject(result);
                if (json.has("result")) {
                    if (json.has("message")) {
                        if (!json.getBoolean("result")) {
                            log.error(json.getString("message"));
                        }
                    }
                } else {
                    this.message = result;
                }
            } catch (Exception e) {
                this.message = "请求的网址不存在，或服务暂停使用中";
            }
        } finally {
            isServerRegister = true;
        }
    }

    public Object getData() {
        return data;
    }

    protected void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public String getRemoteIP() {
        return remoteIP;
    }

    public void setRemoteIP(String remoteIP) {
        this.remoteIP = remoteIP;
    }

    public boolean isResult() {
        return result;
    }

    public void put(String key, String value) {
        this.curl.putParameter(key, value);
    }

    // 获得用户真实的ip地址
    private String getRemoteAddr() {
        if (request == null) {
            log.warn("handle 不存在 request 对象");
            return "127.0.0.1";
        }

        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.split(",")[0];
    }

}
