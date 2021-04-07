package cn.cerc.ui.page.service;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ClassResource;
import cn.cerc.core.ISession;
import cn.cerc.core.IUserLanguage;
import cn.cerc.core.TDateTime;
import cn.cerc.core.Utils;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.ITokenManage;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.db.mysql.Transaction;
import cn.cerc.mis.core.AppClient;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.Handle;
import cn.cerc.mis.core.IForm;
import cn.cerc.mis.core.ISystemTable;
import cn.cerc.mis.core.RequestData;
import cn.cerc.mis.core.SystemBuffer;
import cn.cerc.mis.language.R;
import cn.cerc.mis.other.MemoryBuffer;
import cn.cerc.mis.services.SvrUserLogin;
import cn.cerc.ui.SummerUI;

public class SvrAutoLogin implements IUserLanguage {
    private final ClassResource res = new ClassResource(this, SummerUI.ID);
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
            this.setMessage(res.getString(1, "用户代码不允许为空"));
            return false;
        }
        String deviceId = form.getClient().getId();

        ISystemTable systemTable = Application.getBeanDefault(ISystemTable.class, null);
        SqlQuery dsUser = new SqlQuery(handle);
        dsUser.add("select * from %s where Code_='%s'", systemTable.getUserInfo(), userCode);
        dsUser.open();
        if (dsUser.eof()) {
            this.setMessage(String.format(res.getString(2, "该帐号(%s)并不存在，禁止登录！"), userCode));
            return false;
        }

        try (Transaction tx = new Transaction(handle)) {
            ISession session = handle.getSession();
            String sql = String.format(
                    "update %s set LastTime_=now(),Used_=1 where UserCode_='%s' and MachineCode_='%s'",
                    systemTable.getDeviceVerify(), userCode, deviceId);
            handle.getConnection().execute(sql);

            String token = Utils.generateToken();
            session.setProperty(Application.TOKEN, token);
            session.setProperty("deviceId", deviceId);
            session.setProperty(RequestData.TOKEN, token);
            log.info("扫码登录 sid {}", token);

            String userId = dsUser.getString("ID_");
            session.setProperty(Application.userId, userId);
            session.setProperty(Application.bookNo, dsUser.getString("CorpNo_"));
            session.setProperty(Application.userCode, dsUser.getString("Code_"));
            if (dsUser.getBoolean("DiyRole_")) {
                session.setProperty(Application.roleCode, dsUser.getString("Code_"));
            } else {
                session.setProperty(Application.roleCode, dsUser.getString("RoleCode_"));
            }

            // 更新当前用户总数
            SvrUserLogin svrUserLogin = Application.getBean(new Handle(session), SvrUserLogin.class);
            svrUserLogin.updateCurrentUser("unknow", "", form.getClient().getLanguage());

            try (MemoryBuffer buff = new MemoryBuffer(SystemBuffer.User.SessionInfo, userId, deviceId)) {
                buff.setField("UserID_", userId);
                buff.setField("UserCode_", dsUser.getString("Code_"));
                buff.setField("UserName_", dsUser.getString("Name_"));
                buff.setField("LoginTime_", TDateTime.now());
                buff.setField("VerifyMachine", true);
            }

            // 检查设备码
            SvrUserLogin svrLogin = Application.getBean(handle, SvrUserLogin.class);
            svrLogin.enrollMachineInfo(dsUser.getString("CorpNo_"), userCode, deviceId, "浏览器");

            // 设置登录信息
            AppClient client = new AppClient();
            client.setRequest(request);
            client.setToken(token);

            ITokenManage manage = Application.getBeanDefault(ITokenManage.class, session);
            manage.resumeToken(token);

            form.getRequest().setAttribute(RequestData.TOKEN, token);
            ((AppClient) form.getClient()).setToken(token);
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

    @Override
    public String getLanguageId() {
        return R.getLanguageId(this.handle);
    }
}
