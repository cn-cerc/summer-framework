package cn.cerc.mis.security.sapi;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.Utils;
import cn.cerc.db.core.Curl;
import cn.cerc.mis.SummerMIS;
import net.sf.json.JSONObject;

public class JayunAPI {
    private static final Logger log = LoggerFactory.getLogger(JayunAPI.class);
    private static final ClassConfig config = new ClassConfig(JayunAPI.class, SummerMIS.ID);
    private static String jayunHost;
    private static boolean jayunStop = false;
    private static boolean isServerRegister = false;
    public static final String JAYUN_APP_KEY = "jayun.appKey";
    public static final String JAYUN_APP_SECRET = "jayun.appSecret";
    private Object data;
    private String message;
    private boolean result;

    // 停止调用聚安

    private HttpServletRequest request;
    private String remoteIP;
    private Curl curl;

    static {
        jayunHost = config.getString("jayun.host", "https://www.jayun.site");
        jayunStop = config.getBoolean("jayun.stop", false);
    }

    public JayunAPI(HttpServletRequest request) {
        this.request = request;
        this.remoteIP = getRemoteAddr();
        this.curl = new Curl();

        String appKey = config.getString(JAYUN_APP_KEY, null);
        if (Utils.isNotEmpty(appKey)) {
            this.put("appKey", appKey);
        }
    }

    private void init() {
        if (isServerRegister)
            return;
        try {
            String appKey = config.getString(JAYUN_APP_KEY, null);
            String appSecret = config.getString(JAYUN_APP_SECRET, null);
            if (Utils.isEmpty(appKey)) {
                log.error("jayun.appKey 未设置，无法自动注册 ");
                return;
            }
            if (Utils.isEmpty(appSecret)) {
                log.error("jayun.appSecret 未设置，无法自动注册 ");
                return;
            }
            try {
                String reqUrl = String.format("%s/api/%s", JayunAPI.jayunHost, "server.register");
                Curl curl = new Curl();
                curl.put("appKey", appKey);
                curl.put("appSecret", appSecret);
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

    public void post(String serviceUrl) {
        if (JayunAPI.jayunStop) {
            this.result = true;
            this.message = "聚安配置已关闭";
            return;
        }

        this.init();

        this.result = false;
        this.message = null;
        try {
            String reqUrl = String.format("%s/api/%s", JayunAPI.jayunHost, serviceUrl);
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
        this.curl.put(key, value);
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
