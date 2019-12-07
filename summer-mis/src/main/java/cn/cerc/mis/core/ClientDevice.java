package cn.cerc.mis.core;

import cn.cerc.core.Utils;
import cn.cerc.mis.language.LanguageType;
import cn.cerc.mis.other.BufferType;
import cn.cerc.mis.other.MemoryBuffer;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * // TODO: 2019/12/7 建议更名为 AppClient
 */
@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
public class ClientDevice implements IClient, Serializable {

    private static final long serialVersionUID = -3593077761901636920L;

    public static final String APP_CLIENT_ID = "CLIENTID";
    public static final String APP_DEVICE_TYPE = "device";
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
        return deviceId == null ? RequestData.webclient : deviceId;
    }

    public void setId(String value) {
        this.deviceId = value;
        request.setAttribute(APP_CLIENT_ID, deviceId == null ? "" : deviceId);
        request.getSession().setAttribute(APP_CLIENT_ID, value);
        if (value != null && value.length() == 28) {
            setDevice(APP_DEVICE_PHONE);
        }

        if (token != null && deviceId != null && !"".equals(deviceId)) {
            try (MemoryBuffer buff = new MemoryBuffer(BufferType.getDeviceInfo, token)) {
                getValue(buff, APP_CLIENT_ID, deviceId);
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
        return device == null ? APP_DEVICE_PC : device;
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
        return token != null && "".equals(token) ? null : token;
    }

    public void setToken(String value) {
        String tmp = Utils.isEmpty(value) ? null : value;
        if (tmp != null) {
            try (MemoryBuffer buff = new MemoryBuffer(BufferType.getDeviceInfo, tmp)) {
                // 设备ID
                deviceId = getValue(buff, APP_CLIENT_ID, this.deviceId);
                // 设备样式
                device = getValue(buff, APP_DEVICE_TYPE, this.device);
            }
        } else if (tmp == null) {
            if (this.token != null && !"".equals(this.token)) {
                MemoryBuffer.delete(BufferType.getDeviceInfo, this.token);
            }
        }

        this.token = tmp;
        request.getSession().setAttribute(RequestData.appSession_Key, this.token);
        request.setAttribute(RequestData.appSession_Key, this.token == null ? "" : this.token);
    }

    /**
     * 清空token信息
     * <p>
     * TODO: 2019/12/7 考虑要不要加上缓存一起清空
     */
    public void clear() {
        this.token = null;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("token:").append(token).append(", ");
        buffer.append("deviceId:").append(deviceId).append(", ");
        buffer.append("deviceType:").append(device);
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
        return request;
    }

    @Override
    public void setRequest(HttpServletRequest request) {
        this.request = request;

        // 保存设备类型
        device = request.getParameter(APP_DEVICE_TYPE);
        if (device == null || "".equals(device)) {
            device = (String) request.getSession().getAttribute(APP_DEVICE_TYPE);
        }
        if (device != null && !"".equals(device)) {
            request.getSession().setAttribute(APP_DEVICE_TYPE, device);
        }
        request.setAttribute(APP_DEVICE_TYPE, device == null ? "" : device);

        // 保存并取得device_id
        deviceId = request.getParameter(APP_CLIENT_ID);
        if (deviceId == null || "".equals(deviceId)) {
            deviceId = (String) request.getSession().getAttribute(APP_CLIENT_ID);
        }

        request.setAttribute(APP_CLIENT_ID, deviceId);
        request.getSession().setAttribute(APP_CLIENT_ID, deviceId);

        languageId = request.getParameter(Application.deviceLanguage);
        if (languageId == null || "".equals(languageId)) {
            languageId = (String) request.getSession().getAttribute(Application.deviceLanguage);
        }

        request.setAttribute(Application.deviceLanguage, languageId);
        request.getSession().setAttribute(Application.deviceLanguage, languageId);

        // 取得并保存token
        String token = request.getParameter(RequestData.appSession_Key);
        if (token == null || "".equals(token)) {
            token = (String) request.getSession().getAttribute(RequestData.appSession_Key);
        }

        // 设置sid
        setToken(token);
    }
}
