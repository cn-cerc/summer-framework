package cn.cerc.mis.core;

import javax.servlet.http.HttpServletRequest;

public interface IClient {

    // 判断是否为手机
    boolean isPhone();

    // 返回设备Id
    String getId();

    // 返回设备型号
    String getDevice();

    // 设置设备型号
    void setDevice(String device);

    // 返回设备语言
    String getLanguage();

    // 设置request的attribute信息
    void setRequest(HttpServletRequest request);

}
