package cn.cerc.mis.core;

import cn.cerc.core.ClassResource;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

@Slf4j
public class RequestData {
    private static final ClassResource res = new ClassResource("summer-mvc", RequestData.class);

    // FIXME: 2019/12/7 sid 应该改为 token
    public static final String TOKEN = "sid";
    public static final String WEBCLIENT = "webclient";

    private final String token;
    private String param;
    private String serviceCode;

    public RequestData(HttpServletRequest request) {
        this.token = request.getParameter(RequestData.TOKEN);
        this.serviceCode = request.getParameter("class");
        if (this.serviceCode == null) {
            try {
                this.serviceCode = request.getPathInfo().substring(1);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(res.getString(1, "服务名不能为空！"));
            }
        }
        BufferedReader reader;
        try {
            reader = request.getReader();
            StringBuilder params = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                params.append(line);
            }

            this.param = params.toString();
            if (this.param.length() > 1 && this.param.startsWith("[")) {
                if (this.param.endsWith("]\r\n")) {
                    this.param = this.param.substring(1, this.param.length() - 3);
                } else if (this.param.endsWith("]")) {
                    this.param = this.param.substring(1, this.param.length() - 1);
                }
            }

            if ("".equals(this.param)) {
                this.param = null;
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            this.param = null;
        }
    }

    public String getToken() {
        return token;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getService() {
        return serviceCode;
    }

    public void setService(String service) {
        this.serviceCode = service;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getMethod() {
        String[] sl = serviceCode.split("\\.");
        if (sl.length > 1) {
            return sl[1];
        } else {
            return "execute";
        }
    }

}
