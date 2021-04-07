package cn.cerc.mis.client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.cerc.core.DataSet;
import cn.cerc.mis.core.IPage;

public class ResponseData {
    private static final Logger log = LoggerFactory.getLogger(ResponseData.class);
    public final String outMsg = "{\"result\":%s,\"message\":\"%s\"}";

    private boolean result;
    private String message;
    private String data;

    private HttpServletResponse response;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    private String getError(String message) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("result", false);
        json.put("message", message);
        return json.toString();
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        root.put("result", result);
        root.put("message", message);
        if (data == null) {
            return root.toString();
        }
        StringBuilder builder = new StringBuilder();
        builder.append(root.toString());
        String str = builder.substring(0, builder.length() - 1) + ",\"data\":" + this.data + "}";

        try {
            // 效验返回格式是否为JSON格式
            JsonNode json = mapper.readTree(str);
            if (json.get("result").asBoolean() == this.result) {
                return str;
            } else {
                return getError("result json-data format error");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return getError(e.getMessage());
        }

    }

    public void setDataOut(DataSet dataOut) {
        this.data = String.format("[%s]", dataOut.getJSON());
    }

    public IPage setResultMessage(boolean result, String message) {
        this.result = result;
        this.message = message;
        if (this.response != null) {
            out();
        }
        return null;
    }

    public IPage setResultMessage(boolean result, String format, Object... args) {
        this.result = result;
        this.message = String.format(format, args);
        if (this.response != null) {
            out();
        }
        return null;
    }

    public void out() {
        try {
            PrintWriter writer = getResponse().getWriter();
            writer.print(this.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
