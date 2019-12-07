package cn.cerc.mis.message;

import cn.cerc.core.DataSet;
import cn.cerc.core.IHandle;
import cn.cerc.db.jiguang.ClientType;
import cn.cerc.db.jiguang.JiguangPush;
import cn.cerc.mis.core.LocalService;

/**
 * 往指定用户的所有移动设备发送消息
 */
public class JPushRecord {
    private String corpNo;
    private String userCode;
    private String title;
    private String alert;
    private String msgId;
    // 极光推送默认声音
    private String sound = "default";

    public JPushRecord(String corpNo, String userCode, String msgId) {
        this.corpNo = corpNo;
        this.userCode = userCode;
        this.msgId = msgId;
    }

    public void send(IHandle handle) {
        LocalService svr = new LocalService(handle, "SvrUserLogin.getMachInfo");
        if (!svr.exec("CorpNo_", corpNo, "UserCode_", userCode)) {
            throw new RuntimeException(svr.getMessage());
        }

        // 设置极光推送平台
        JiguangPush push = new JiguangPush(handle);
        push.setMessage(alert);
        push.setMsgId("" + msgId);
        push.setTitle(title);

        // 将消息推送到极光平台
        DataSet dataOut = svr.getDataOut();
        while (dataOut.fetch()) {
            String machineCode = dataOut.getString("MachineCode_");
            int machineType = dataOut.getInt("MachineType_");
            switch (machineType) {
            case 6:
                push.send(ClientType.IOS, machineCode, this.sound);
                break;
            case 7:
                // 过滤掉没有注册IMEI码的移动设备
                if (!"n_null".equals(machineCode) && !"n_000000000000000".equals(machineCode)) {
                    push.send(ClientType.Android, machineCode, this.sound);
                }
                break;
            default:
                break;
            }
        }
    }

    public String getCorpNo() {
        return corpNo;
    }

    public JPushRecord setCorpNo(String corpNo) {
        this.corpNo = corpNo;
        return this;
    }

    public String getUserCode() {
        return userCode;
    }

    public JPushRecord setUserCode(String userCode) {
        this.userCode = userCode;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public JPushRecord setTitle(String title) {
        this.title = title;
        return this;
    }

    public JPushRecord setTitle(String format, Object... args) {
        this.title = String.format(format, args);
        return this;
    }

    public String getAlert() {
        return alert;
    }

    public JPushRecord setAlert(String alert) {
        this.alert = alert;
        return this;
    }

    public JPushRecord setAlert(String format, Object... args) {
        this.alert = String.format(format, args);
        return this;
    }

    public String getMsgId() {
        return msgId;
    }

    public JPushRecord setMsgId(String msgId) {
        this.msgId = msgId;
        return this;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

}
