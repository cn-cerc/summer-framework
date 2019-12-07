package cn.cerc.mis.page.service;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.IHandle;
import cn.cerc.core.TDateTime;
import cn.cerc.core.Utils;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.db.mysql.Transaction;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.ClientDevice;
import cn.cerc.mis.core.HandleDefault;
import cn.cerc.mis.core.IForm;
import cn.cerc.mis.core.ISystemTable;
import cn.cerc.mis.core.RequestData;
import cn.cerc.mis.other.BufferType;
import cn.cerc.mis.other.MemoryBuffer;
import cn.cerc.mis.services.SvrUserLogin;

public class SvrAutoLogin {
    private static final Logger log = LoggerFactory.getLogger(SvrAutoLogin.class);

    private IHandle handle;
    private String message;

    public SvrAutoLogin(IHandle handle) {
        super();
        this.handle = handle;
    }

    public boolean check(IForm form, HttpServletRequest request) {
        String userCode = request.getParameter("userCode");
        if (userCode == null || "".equals(userCode)) {
            this.setMessage("用户代码不允许为空");
            return false;
        }
        String deviceId = form.getClient().getId();

        ISystemTable systemTable = Application.getBean("systemTable", ISystemTable.class);
        SqlQuery dsUser = new SqlQuery(handle);
        dsUser.add("select * from %s where Code_='%s'", systemTable.getUserInfo(), userCode);
        dsUser.open();
        if (dsUser.eof()) {
            this.setMessage(String.format("该帐号(%s)并不存在，禁止登录！", userCode));
            return false;
        }

        try (Transaction tx = new Transaction(handle)) {
            HandleDefault sess = (HandleDefault) handle.getProperty(null);
            String sql = String.format(
                    "update %s set LastTime_=now(),Used_=1 where UserCode_='%s' and MachineCode_='%s'",
                    systemTable.getDeviceVerify(), userCode, deviceId);
            sess.getConnection().execute(sql);

            String token = Utils.guidFixStr();
            sess.setProperty(Application.token, token);
            sess.setProperty("deviceId", deviceId);
            sess.setProperty("sid", token);
            log.info("扫码登录 sid {}", token);

            String userId = dsUser.getString("ID_");
            sess.setProperty(Application.userId, userId);
            sess.setProperty(Application.bookNo, dsUser.getString("CorpNo_"));
            sess.setProperty(Application.userCode, dsUser.getString("Code_"));
            if (dsUser.getBoolean("DiyRole_")) {
                sess.setProperty(Application.roleCode, dsUser.getString("Code_"));
            } else {
                sess.setProperty(Application.roleCode, dsUser.getString("RoleCode_"));
            }

            // 更新当前用户总数
            SvrUserLogin svrUserLogin = Application.getBean(sess, SvrUserLogin.class);
            svrUserLogin.updateCurrentUser("unknow", "", form.getClient().getLanguage());

            try (MemoryBuffer buff = new MemoryBuffer(BufferType.getSessionInfo, userId, deviceId)) {
                buff.setField("UserID_", userId);
                buff.setField("UserCode_", dsUser.getString("Code_"));
                buff.setField("UserName_", dsUser.getString("Name_"));
                buff.setField("LoginTime_", TDateTime.Now());
                buff.setField("VerifyMachine", true);
            }

            // 检查设备码
            SvrUserLogin svrLogin = Application.getBean(handle, SvrUserLogin.class);
            svrLogin.enrollMachineInfo(dsUser.getString("CorpNo_"), userCode, deviceId, "浏览器");

            // 设置登录信息
            ClientDevice info = new ClientDevice();
            info.setRequest(request);
            info.setSid(token);
            sess.init(token);

            form.getRequest().setAttribute(RequestData.appSession_Key, token);
            ((ClientDevice) form.getClient()).setSid(token);
            tx.commit();
        }
        return true;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
