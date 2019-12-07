package cn.cerc.mis.core;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import cn.cerc.mis.other.BufferType;
import cn.cerc.mis.other.MemoryBuffer;

@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
public class ClientDevice implements IClient, Serializable {
    // private static final Logger log = Logger.LoggerFactory(DeviceInfo.class);
    private static final long serialVersionUID = -3593077761901636920L;
    private String sid; // application session id;
    private String deviceId; // device id
    private String deviceType; // phone/pad/ee/pc
    private String languageId; // device language: cn/en
    private HttpServletRequest request;

    public static final String deviceId_key = "CLIENTID";
    public static final String deviceType_key = "device";
    // 手机
    public static final String device_phone = "phone";
    public static final String device_android = "android";
    public static final String device_iphone = "iphone";
    public static final String device_weixin = "weixin";
    // 平板
    public static final String device_pad = "pad";
    // 电脑
    public static final String device_pc = "pc";
    // 电脑专用浏览器
    public static final String device_ee = "ee";

    public ClientDevice() {
        super();
    }

    private String getValue(MemoryBuffer buff, String key, String def) {
        String result = def;
        String tmp = buff.getString(key);
        // 如果缓存有值，则从缓存中取值，且当def无值时，返回缓存值
        if (tmp != null && !"".equals(tmp)) {
            if (def == null || "".equals(def))
                result = tmp;
        }
        // 如果def有值，且与缓存不同时，更新缓存
        if (def != null && !"".equals(def)) {
            if (tmp == null || !tmp.equals(def))
                buff.setField(key, def);
        }
        return result;
    }

    @Override
    public String getId() {
        return deviceId == null ? RequestData.webclient : deviceId;
    }

    @Override
    public String getDevice() {
        return deviceType == null ? device_pc : deviceType;
    }

    @Override
    public String getLanguage() {
        return languageId == null ? "cn" : languageId;
    }

    @Override
    public void setDevice(String deviceType) {
        if (deviceType == null || "".equals(deviceType))
            return;

        this.deviceType = deviceType;
        request.setAttribute(deviceType_key, deviceType == null ? "" : deviceType);
        request.getSession().setAttribute(deviceType_key, deviceType);
        if (sid != null && deviceType != null && !"".equals(deviceType)) {
            try (MemoryBuffer buff = new MemoryBuffer(BufferType.getDeviceInfo, sid)) {
                getValue(buff, deviceType_key, deviceType);
            }
        }
        return;
    }

    public void setSid(String value) {
        String tmp = value == null || "".equals(value) ? null : value;
        if (tmp != null) {
            // device_id = (String)
            // req.getSession().getAttribute(deviceId_key);
            try (MemoryBuffer buff = new MemoryBuffer(BufferType.getDeviceInfo, tmp)) {
                // 设备ID
                deviceId = getValue(buff, deviceId_key, deviceId);
                // 设备样式
                deviceType = getValue(buff, deviceType_key, deviceType);
            }
        } else if (tmp == null) {
            if (this.sid != null && !"".equals(this.sid)) {
                MemoryBuffer.delete(BufferType.getDeviceInfo, this.sid);
            }
        }
        this.sid = tmp;
        request.getSession().setAttribute(RequestData.appSession_Key, this.sid);
        request.setAttribute(RequestData.appSession_Key, this.sid == null ? "" : this.sid);
    }

    public void setId(String value) {
        this.deviceId = value;
        request.setAttribute(deviceId_key, deviceId == null ? "" : deviceId);
        request.getSession().setAttribute(deviceId_key, value);
        if (value != null && value.length() == 28)
            setDevice(device_phone);
        if (sid != null && deviceId != null && !"".equals(deviceId)) {
            try (MemoryBuffer buff = new MemoryBuffer(BufferType.getDeviceInfo, sid)) {
                getValue(buff, deviceId_key, deviceId);
            }
        }
    }

    public String getSid() {
        return sid != null && "".equals(sid) ? null : sid;
    }

    public void clear() {
        this.sid = null;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("sid:").append(sid).append(", ");
        sb.append("deviceId:").append(deviceId).append(", ");
        sb.append("deviceType:").append(deviceType);
        return sb.toString();
    }

    @Override
    public boolean isPhone() {
        return device_phone.equals(getDevice()) || device_android.equals(getDevice())
                || device_iphone.equals(getDevice()) || device_weixin.equals(getDevice());
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    @Override
    public void setRequest(HttpServletRequest request) {
        this.request = request;
        // 保存设备类型
        deviceType = request.getParameter(deviceType_key);
        if (deviceType == null || "".equals(deviceType))
            deviceType = (String) request.getSession().getAttribute(deviceType_key);
        if (deviceType != null && !"".equals(deviceType))
            request.getSession().setAttribute(deviceType_key, deviceType);
        request.setAttribute(deviceType_key, deviceType == null ? "" : deviceType);

        // 保存并取得device_id
        deviceId = request.getParameter(deviceId_key);
        if (deviceId == null || "".equals(deviceId))
            deviceId = (String) request.getSession().getAttribute(deviceId_key);

        request.setAttribute(deviceId_key, deviceId);
        request.getSession().setAttribute(deviceId_key, deviceId);

        languageId = request.getParameter(Application.deviceLanguage);
        if (languageId == null || "".equals(languageId))
            languageId = (String) request.getSession().getAttribute(Application.deviceLanguage);

        request.setAttribute(Application.deviceLanguage, languageId);
        request.getSession().setAttribute(Application.deviceLanguage, languageId);

        // 取得并保存token(sid)
        String sid = request.getParameter(RequestData.appSession_Key);
        if (sid == null || "".equals(sid))
            sid = (String) request.getSession().getAttribute(RequestData.appSession_Key);
        // 设置sid
        setSid(sid);
    }
}
