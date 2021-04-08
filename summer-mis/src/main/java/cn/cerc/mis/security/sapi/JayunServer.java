package cn.cerc.mis.security.sapi;

import javax.servlet.http.HttpServletRequest;

public class JayunServer {
    private HttpServletRequest request;
    private String message;

    public JayunServer(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * 向聚安云注册当前服务器的互联网IP地址，是后续所有调用的基础
     * <p>
     * 已改为自动注册，不再需要自动调用子函数，已有调用请尽快移除
     * <p>
     * 下个版本将移除该函数，请尽快配置您的
     * <p>
     * jayun.appKey
     * <p>
     * jayun.appSecret
     *
     * @param appKey    聚安云应用Id
     * @param appSecret 聚安云应用秘钥
     * @return 调用成功时返回 true
     */
    public boolean register(String appKey, String appSecret) {
        JayunAPI api = new JayunAPI(request);
        api.put("appKey", appKey);
        api.put("appSecret", appSecret);
        api.post("server.register");
        this.setMessage(api.getMessage());
        return api.isResult();
    }

    /**
     * 根据 appKey 获取最靠近调用者的主机IP
     *
     * @return 调用成功时返回 true
     */
    public boolean getIP() {
        JayunAPI api = new JayunAPI(request);
        api.put("ip", api.getRemoteIP());
        api.post("server.getIP");
        this.setMessage(api.getMessage());
        return api.isResult();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
