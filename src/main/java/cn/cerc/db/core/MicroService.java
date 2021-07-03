package cn.cerc.db.core;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MicroService extends Curl {
    /**
     * 服务主机的地址
     */
    private String host = "http://127.0.0.1";
    /**
     * 服务主机的port
     */
    private int port = 80;
    /**
     * 服务前缀
     */
    private String path;
    /**
     * 返回结果
     */
    private boolean result;
    /**
     * 返回消息
     */
    private String message;
    /**
     * 返回内容转为gson
     */
    private JsonObject response;

    public static void main(String[] args) {
        MicroService service = new MicroService();
        service.put("appKey", "asdfsdf");

        System.out.println(service.post("server.getIP"));
        System.out.println(service.isResult());
        System.out.println(service.getMessage());
        System.out.println(service.getResponseContent());
        System.out.println(service.getResponse());
    }

    /**
     * 执行指定的服务
     *
     * @param serviceCode 服务代码
     * @return 返回成功与否
     */
    public boolean get(String serviceCode) {
        String url = getServiceUrl(serviceCode);
        String text = this.doGet(url);
        this.response = JsonParser.parseString(text).getAsJsonObject();
        if (response.has("result")) {
            this.result = response.get("result").getAsBoolean();
        }
        if (response.has("message")) {
            this.message = response.get("message").getAsString();
        }
        return this.result;
    }

    /**
     * 执行指定的服务
     *
     * @param serviceCode 服务代码
     * @return 返回成功与否
     */
    public boolean post(String serviceCode) {
        String url = getServiceUrl(serviceCode);
        this.response = null;
        try {
            String text = this.doPost(url);
            this.response = JsonParser.parseString(text).getAsJsonObject();
            if (response.has("result")) {
                this.result = response.get("result").getAsBoolean();
            }
            if (response.has("message")) {
                this.message = response.get("message").getAsString();
            }
            return this.result;
        } catch (IOException e) {
            e.printStackTrace();
            this.message = e.getMessage();
            return false;
        }
    }

    private String getServiceUrl(String serviceCode) {
        StringBuffer sb = new StringBuffer();
        sb.append(this.host);
        if (port != 80) {
            sb.append(":" + this.port);
        }
        if (this.path != null) {
            sb.append(this.path);
        }
        sb.append("/" + serviceCode);
        return sb.toString();
    }

    public String getMessage() {
        return message;
    }

    public boolean isResult() {
        return result;
    }

    public JsonObject getResponse() {
        return response;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
