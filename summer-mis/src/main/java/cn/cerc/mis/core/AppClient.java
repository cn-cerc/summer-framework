package cn.cerc.mis.core;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import cn.cerc.core.Utils;
import cn.cerc.mis.other.MemoryBuffer;

@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
public class AppClient implements IClient, Serializable {
    private static final Logger log = LoggerFactory.getLogger(AppClient.class);
    private static final long serialVersionUID = -3593077761901636920L;

    public static final String CLIENT_ID = "CLIENTID";
    public static final String DEVICE = "device";

    // 手机
    public static final String phone = "phone";
    public static final String android = "android";
    public static final String iphone = "iphone";
    public static final String wechat = "weixin";
    // 平板
    public static final String pad = "pad";
    // 电脑
    public static final String pc = "pc";
    // 客户端专用浏览器
    public static final String ee = "ee";

    private String token; // application session id;
    private String deviceId; // device id
    private String device; // phone/pad/ee/pc
    private String languageId; // device language: cn/en
    private HttpServletRequest request;

    private String getValue(MemoryBuffer buff, String key, String def) {
        String result = def;
        String tmp = buff.getString(key);

        // 如果缓存有值，则从缓存中取值，且当def无值时，返回缓存值
        if (tmp != null && !"".equals(tmp)) {
            if (def == null || "".equals(def)) {
                result = tmp;
            }
        }

        // 如果def有值，且与缓存不同时，更新缓存
        if (def != null && !"".equals(def)) {
            if (tmp == null || !tmp.equals(def)) {
                buff.setField(key, def);
            }
        }
        return result;
    }

    @Override
    public String getId() {
        return this.deviceId == null ? RequestData.WEBCLIENT : this.deviceId;
    }

    public void setId(String value) {
        this.deviceId = value;
        request.setAttribute(CLIENT_ID, this.deviceId == null ? "" : this.deviceId);
        request.getSession().setAttribute(CLIENT_ID, value);
        if (value != null && value.length() == 28) {
            setDevice(phone);
        }

        if (token != null && this.deviceId != null && !"".equals(this.deviceId)) {
            try (MemoryBuffer buff = new MemoryBuffer(SystemBuffer.Token.DeviceInfo, token)) {
                getValue(buff, CLIENT_ID, this.deviceId);
            }
        }
    }

    /**
     * 设备类型默认是 pc
     *
     * @return device
     */
    @Override
    public String getDevice() {
        return this.device == null ? pc : device;
    }

    @Override
    public void setDevice(String device) {
        if (device == null || "".equals(device)) {
            return;
        }

        // 更新类属性
        this.device = device;

        // 更新request属性
        request.setAttribute(DEVICE, device == null ? "" : device);
        request.getSession().setAttribute(DEVICE, device);

        // 更新设备缓存
        if (token != null) {
            try (MemoryBuffer buff = new MemoryBuffer(SystemBuffer.Token.DeviceInfo, token)) {
                getValue(buff, DEVICE, device);
            }
        }
        return;
    }

    @Override
    public String getLanguage() {
        return languageId == null ? Application.App_Language : languageId;
    }

    public String getToken() {
        return "".equals(token) ? null : token;
    }

    /**
     * 清空token信息
     * <p>
     * TODO: 2019/12/7 考虑要不要加上缓存一起清空
     */
    public void clear() {
        if (Utils.isNotEmpty(token)) {
            try (MemoryBuffer buff = new MemoryBuffer(SystemBuffer.Token.DeviceInfo, token)) {
                buff.clear();
            }
            try (MemoryBuffer buff = new MemoryBuffer(SystemBuffer.Token.SessionBase, token)) {
                buff.clear();
            }
        }
        this.token = null;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("token:").append(this.token).append(", ");
        buffer.append("deviceId:").append(this.deviceId).append(", ");
        buffer.append("deviceType:").append(this.device);
        return buffer.toString();
    }

    @Override
    public boolean isPhone() {
        return phone.equals(getDevice()) || android.equals(getDevice()) || iphone.equals(getDevice())
                || wechat.equals(getDevice());
    }

    public boolean isNotPhone() {
        return !isPhone();
    }

    public HttpServletRequest getRequest() {
        return this.request;
    }

    @Override
    public void setRequest(HttpServletRequest request) {
        this.request = request;

        // 保存设备类型
        this.device = request.getParameter(DEVICE);
        if (this.device == null || "".equals(this.device)) {
            this.device = (String) request.getSession().getAttribute(DEVICE);
        }
        if (this.device != null && !"".equals(this.device)) {
            request.getSession().setAttribute(DEVICE, this.device);
        }
        request.setAttribute(DEVICE, this.device == null ? "" : this.device);

        // 保存并取得 CLIENTID
        this.deviceId = request.getParameter(CLIENT_ID);
        if (this.deviceId == null || "".equals(this.deviceId)) {
            this.deviceId = (String) request.getSession().getAttribute(CLIENT_ID);
        }

        request.setAttribute(CLIENT_ID, this.deviceId);
        request.getSession().setAttribute(CLIENT_ID, this.deviceId);

        this.languageId = request.getParameter(Application.deviceLanguage);
        if (this.languageId == null || "".equals(this.languageId)) {
            this.languageId = (String) request.getSession().getAttribute(Application.deviceLanguage);
        }

        request.setAttribute(Application.deviceLanguage, this.languageId);
        request.getSession().setAttribute(Application.deviceLanguage, this.languageId);

        // 取得并保存token
        String token = request.getParameter(RequestData.TOKEN);// 获取客户端的 token
        if (token == null || "".equals(token)) {
            token = (String) request.getSession().getAttribute(RequestData.TOKEN); // 获取服务端的 token
            // 设置token
            if (Utils.isEmpty(token)) {
                log.debug("get token from request attribute is empty");
            } else {
                log.debug("get token from request attribute is {}", token);
            }
        }
        log.debug("request session id {}", request.getSession().getId());

        setToken(token);
    }

    /**
     * 设置token的值到session
     */
    public void setToken(String value) {
        String token = Utils.isEmpty(value) ? null : value;
        if (token != null) {
            // 判断缓存是否过期
            try (MemoryBuffer buff = new MemoryBuffer(SystemBuffer.Token.DeviceInfo, token)) {
                // 设备ID
                this.deviceId = getValue(buff, CLIENT_ID, this.deviceId);
                // 设备类型
                this.device = getValue(buff, DEVICE, this.device);
            }
        } else {
            if (this.token != null && !"".equals(this.token)) {
                log.warn("the param value is null，delete the token of cache: {}", this.token);
                MemoryBuffer.delete(SystemBuffer.Token.DeviceInfo, this.token);
            }
        }
        log.debug("sessionID 2: {}", request.getSession().getId());

        this.token = token;
        request.getSession().setAttribute(RequestData.TOKEN, this.token);
        request.setAttribute(RequestData.TOKEN, this.token == null ? "" : this.token);
    }

    /**
     * @param request HttpServletRequest
     * @return 获取客户端的访问地址
     */
    public static String getIP(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "0.0.0.0";
        }
        return ip;
    }

}
