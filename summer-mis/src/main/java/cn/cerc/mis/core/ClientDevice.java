package cn.cerc.mis.core;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import cn.cerc.core.Utils;
import cn.cerc.mis.language.LanguageType;
import cn.cerc.mis.other.BufferType;
import cn.cerc.mis.other.MemoryBuffer;
import lombok.extern.slf4j.Slf4j;

/**
 * // TODO: 2019/12/7 建议更名为 AppClient
 */
@Slf4j
@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
public class ClientDevice implements IClient, Serializable {

    private static final long serialVersionUID = -3593077761901636920L;

    public static final String APP_CLIENT_ID = "CLIENTID";
    public static final String APP_DEVICE_TYPE = "device";
    public static final String APP_CLIENTID_ID = "clientId";
    // 手机
    public static final String APP_DEVICE_PHONE = "phone";
    public static final String APP_DEVICE_ANDROID = "android";
    public static final String APP_DEVICE_IPHONE = "iphone";
    public static final String APP_DEVICE_WEIXIN = "weixin";
    // 平板
    public static final String APP_DEVICE_PAD = "pad";
    // 电脑
    public static final String APP_DEVICE_PC = "pc";
    // 客户端专用浏览器
    public static final String APP_DEVICE_EE = "ee";

    private String token; // application session id;
    private String deviceId; // device id
    private String device; // phone/pad/ee/pc
    private String languageId; // device language: cn/en
    private HttpServletRequest request;

    public ClientDevice() {
        super();
    }

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
        request.setAttribute(APP_CLIENT_ID, this.deviceId == null ? "" : this.deviceId);
        request.getSession().setAttribute(APP_CLIENT_ID, value);
        if (value != null && value.length() == 28) {
            setDevice(APP_DEVICE_PHONE);
        }

        if (token != null && this.deviceId != null && !"".equals(this.deviceId)) {
            try (MemoryBuffer buff = new MemoryBuffer(BufferType.getDeviceInfo, token)) {
                getValue(buff, APP_CLIENT_ID, this.deviceId);
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
        return this.device == null ? APP_DEVICE_PC : device;
    }

    @Override
    public void setDevice(String device) {
        if (device == null || "".equals(device)) {
            return;
        }

        // 更新类属性
        this.device = device;

        // 更新request属性
        request.setAttribute(APP_DEVICE_TYPE, device == null ? "" : device);
        request.getSession().setAttribute(APP_DEVICE_TYPE, device);

        // 更新设备缓存
        if (token != null) {
            try (MemoryBuffer buff = new MemoryBuffer(BufferType.getDeviceInfo, token)) {
                getValue(buff, APP_DEVICE_TYPE, device);
            }
        }
        return;
    }

    @Override
    public String getLanguage() {
        return languageId == null ? LanguageType.zh_CN : languageId;
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
            try (MemoryBuffer buff = new MemoryBuffer(BufferType.getDeviceInfo, token)) {
                buff.clear();
            }
            try (MemoryBuffer buff = new MemoryBuffer(BufferType.getSessionBase, token)) {
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
        return APP_DEVICE_PHONE.equals(getDevice()) || APP_DEVICE_ANDROID.equals(getDevice())
                || APP_DEVICE_IPHONE.equals(getDevice()) || APP_DEVICE_WEIXIN.equals(getDevice());
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
        this.device = request.getParameter(APP_DEVICE_TYPE);
        if (this.device == null || "".equals(this.device)) {
            this.device = (String) request.getSession().getAttribute(APP_DEVICE_TYPE);
        }
        if (this.device != null && !"".equals(this.device)) {
            request.getSession().setAttribute(APP_DEVICE_TYPE, this.device);
        }
        request.setAttribute(APP_DEVICE_TYPE, this.device == null ? "" : this.device);

        // 保存并取得device_id
        this.deviceId = request.getParameter(APP_CLIENT_ID);
        if (this.deviceId == null || "".equals(this.deviceId)) {
            this.deviceId = (String) request.getSession().getAttribute(APP_CLIENT_ID);
        }

        request.setAttribute(APP_CLIENT_ID, this.deviceId);
        request.getSession().setAttribute(APP_CLIENT_ID, this.deviceId);

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
        }
        log.debug("sessionID 1: {}", request.getSession().getId());

        // 设置token
        setToken(token);
    }

    public void setToken(String value) {
        String token = Utils.isEmpty(value) ? null : value;
        if (token != null) {
            try (MemoryBuffer buff = new MemoryBuffer(BufferType.getDeviceInfo, token)) {
                // 设备ID
                this.deviceId = getValue(buff, APP_CLIENT_ID, this.deviceId);
                // 设备类型
                this.device = getValue(buff, APP_DEVICE_TYPE, this.device);
            }
        } else {
            if (this.token != null && !"".equals(this.token)) {
                MemoryBuffer.delete(BufferType.getDeviceInfo, this.token);
            }
        }

        log.debug("sessionID 2: {}", request.getSession().getId());
        this.token = token;
        request.getSession().setAttribute(RequestData.TOKEN, this.token);
        request.setAttribute(RequestData.TOKEN, this.token == null ? "" : this.token);
    }
}
