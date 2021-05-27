package cn.cerc.mis.core;

import cn.cerc.db.core.IHandle;

@Deprecated
public class ClientDevice extends AppClient {
    private static final long serialVersionUID = 3637149857407787553L;
    public static final String device_pc = pc;
    public static final String deviceId_key = CLIENT_ID;
    public static final String deviceType_key = DEVICE;

    public ClientDevice() {
        super();
    }

    public ClientDevice(IHandle handle) {
        super();
    }
}
