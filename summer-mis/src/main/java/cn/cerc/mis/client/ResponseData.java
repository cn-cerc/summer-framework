package cn.cerc.mis.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;

public class ResponseData {
    private static final Logger log = LoggerFactory.getLogger(ResponseData.class);
    public final String outMsg = "{\"result\":%s,\"message\":\"%s\"}";

    private boolean state;
    private String message;
    private String data;

    public boolean isResult() {
        return state;
    }

    public void setResult(boolean result) {
        this.state = result;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    private String getError(String message) {
        JSONObject json = new JSONObject();
        json.put("result", false);
        json.put("message", message);
        return json.toString();
    }

    public String toString() {
        JSONObject json = new JSONObject();
        json.put("result", state);
        json.put("message", message);

        if (data == null)
            return json.toString();

        StringBuffer tmp = new StringBuffer();
        tmp.append(json.toString());
        String str = tmp.substring(0, tmp.length() - 1) + ",\"data\":" + this.data + "}";

        try {
            // 效验返回格式是否为JSON格式
            json = JSONObject.fromObject(str);
            if (json.getBoolean("result") == this.state) {
                return str;
            } else {
                return getError("result json-data format error");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return getError(e.getMessage());
        }

    }
}
