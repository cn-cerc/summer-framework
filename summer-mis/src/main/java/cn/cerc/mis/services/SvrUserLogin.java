package cn.cerc.mis.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.cerc.core.DataSet;
import cn.cerc.core.IHandle;
import cn.cerc.core.MD5;
import cn.cerc.core.Record;
import cn.cerc.core.TDateTime;
import cn.cerc.core.Utils;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.db.jiguang.ClientType;
import cn.cerc.db.mysql.BuildQuery;
import cn.cerc.db.mysql.SqlOperator;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.db.mysql.Transaction;
import cn.cerc.db.oss.OssConnection;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.CustomService;
import cn.cerc.mis.core.DataValidateException;
import cn.cerc.mis.core.HandleDefault;
import cn.cerc.mis.core.LocalService;
import cn.cerc.mis.core.Webfunc;
import cn.cerc.mis.language.R;
import cn.cerc.mis.other.BookVersion;
import cn.cerc.mis.other.BufferType;
import cn.cerc.mis.other.MemoryBuffer;

/**
 * 用于用户登录
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SvrUserLogin extends CustomService {
    private static final Logger log = LoggerFactory.getLogger(SvrUserLogin.class);
    private static String GuidNull = "";
    private static int Max_Viability = 1;
    public static int TimeOut = 5; // 效验代码超时时间（分钟）

    /*
     * 用户登录入口
     */
    @Webfunc
    public boolean Check() throws SecurityCheckException {
        Record headIn = getDataIn().getHead();
        getDataOut().getHead().setField("errorNo", 0);

        String device_name = "";
        if (headIn.hasValue("ClientName_")) {
            device_name = headIn.getString("ClientName_");
        } else {
            device_name = "unknow";
        }

        HandleDefault sess = (HandleDefault) this.getProperty(null);
        if (headIn.exists("ClientIP_")) {
            sess.setProperty(Application.clientIP, headIn.getString("ClientIP_"));
        } else {
            sess.setProperty(Application.clientIP, "0.0.0.0");
        }

        // 开始进行用户验证
        String userCode = headIn.getString("Account_");
        if (userCode.equals("")) {
            throw new SecurityCheckException("用户帐号不允许为空！");
        }

        SqlQuery dsUser = new SqlQuery(this);
        dsUser.add("select UID_,CorpNo_,ID_,Code_,Name_,Mobile_,DeptCode_,Enabled_,Password_,BelongAccount_,");
        dsUser.add("VerifyTimes_,Encrypt_,SecurityLevel_,SecurityMachine_,PCMachine1_,PCMachine2_,");
        dsUser.add("PCMachine3_,RoleCode_,DiyRole_ from %s where Code_='%s'", systemTable.getUserInfo(), userCode);
        dsUser.open();
        if (dsUser.eof()) {
            throw new SecurityCheckException(String.format("该帐号(%s)并不存在，禁止登录！", userCode));
        }

        String corpNo = dsUser.getString("CorpNo_");
        ServerConfig config = new ServerConfig();
        String supCorpNo = config.getProperty("vine.mall.supCorpNo", "");
        // 判断该手机号绑定的账号，是否有supCorpNo的下游，专用App登录
        if (!"".equals(supCorpNo)) {
            SqlQuery ds = new SqlQuery(this);
            ds.add("select oi.CorpNo_,oi.ShortName_,a.Code_,a.Name_ from %s a ", systemTable.getUserInfo());
            ds.add("inner join %s oi on a.CorpNo_=oi.CorpNo_", systemTable.getBookInfo());
            ds.add("inner join scmnetaccredit na on na.SupCode_='%s' and na.CusCode_=oi.CorpNo_", supCorpNo);
            ds.add("where a.Enabled_=1 and oi.Status_<3 ");
            if (!"".equals(dsUser.getString("Mobile_"))) {
                ds.add("and a.Mobile_='%s'", dsUser.getString("Mobile_"));
            } else {
                ds.add("and a.Code_='%s'", userCode);
            }
            ds.open();
            if (ds.eof()) {
                throw new SecurityCheckException(String.format("您不是该上游%s的下游客户，不允许登录！", supCorpNo));
            }
        }

        BookInfoRecord buff = MemoryBookInfo.get(this, corpNo);
        if (buff == null) {
            throw new SecurityCheckException(String.format("没有找到注册的帐套  %s ", corpNo));
        }

        String mobile = dsUser.getString("Mobile_");
        getDataOut().getHead().setField("Mobile_", mobile);

        String password = headIn.getString("Password_");
        if (password == null || "".equals(password)) {
            throw new RuntimeException("用户密码不允许为空！");
        }

        boolean YGLogin = buff.getCorpType() == BookVersion.ctFree.ordinal();
        if (buff.getStatus() == 3) {
            throw new SecurityCheckException("对不起，您的账套处于暂停录入状态，禁止登录！若需启用，请您联系客服处理！");
        }
        if (buff.getStatus() == 4) {
            throw new SecurityCheckException("对不起，您的帐套已过期，请联系客服续费！");
        }
        if (dsUser.getInt("Enabled_") < 1 && dsUser.getInt("VerifyTimes_") == 6) {
            throw new SecurityCheckException(
                    String.format("该帐号(%s)因输入错误密码或验证码次数达到6次，已被自动停用，禁止登录！若需启用，请您联系客服处理！", userCode));
        }
        if (dsUser.getInt("Enabled_") < 1) {
            throw new SecurityCheckException(String.format("该帐号(%s)被暂停使用，禁止登录！若需启用，请您联系客服处理！", userCode));
        }
        // 判断此帐号是否为附属帐号
        if (dsUser.getString("BelongAccount_") != null && !"".equals(dsUser.getString("BelongAccount_"))) {
            throw new SecurityCheckException(
                    String.format("该帐号已被设置为附属帐号，不允许登录，请使用主帐号 %s 登录系统！", dsUser.getString("BelongAccount_")));
        }

        // 检查设备码
        String deviceId = headIn.getString("MachineID_");
        enrollMachineInfo(dsUser.getString("CorpNo_"), userCode, deviceId, device_name);

        if (dsUser.getBoolean("Encrypt_")) {
            if (!headIn.exists("wx")) {
                password = MD5.get(dsUser.getString("Code_") + password);
            }
        }

        if (!isAutoLogin(userCode, deviceId)) {
            if (!dsUser.getString("Password_").equals(password)) {
                dsUser.edit();
                if (dsUser.getInt("VerifyTimes_") == 6) {
                    // 该账号设置停用
                    dsUser.setField("Enabled_", 0);
                    dsUser.post();
                    throw new RuntimeException("您输入密码的错误次数已超出规定次数，现账号已被自动停用，若需启用，请您联系客服处理！");
                } else {
                    dsUser.setField("VerifyTimes_", dsUser.getInt("VerifyTimes_") + 1);
                    dsUser.post();
                    if (dsUser.getInt("VerifyTimes_") > 3) {
                        throw new SecurityCheckException(
                                String.format("您输入密码的错误次数已达 %d 次，若忘记密码，可点击下方【忘记密码】链接重新设置密码，输错超过6次时，您的账号将被自动停用！",
                                        dsUser.getInt("VerifyTimes_")));
                    } else {
                        throw new SecurityCheckException("您的登录密码错误，禁止登录！");
                    }
                }
            }
        }

        // 当前设备是否已被停用
        if (!isStopUsed(userCode, deviceId)) {
            throw new SecurityCheckException("您的当前设备已被停用，禁止登录，请联系管理员恢复启用！");
        }

        try (Transaction tx = new Transaction(this)) {
            String sql = String.format(
                    "update %s set LastTime_=now() where UserCode_='%s' and MachineCode_='%s' and Used_=1",
                    systemTable.getDeviceVerify(), userCode, deviceId);
            getConnection().execute(sql);

            // 若该账套是待安装，则改为已启用
            SqlQuery dsCorp = new SqlQuery(this);
            dsCorp.add("select * from %s ", systemTable.getBookInfo());
            dsCorp.add("where CorpNo_='%s' and Status_=1 ", corpNo);
            dsCorp.open();
            if (!dsCorp.eof()) {
                dsCorp.edit();
                dsCorp.setField("Status_", 2);
                dsCorp.post();
                MemoryBookInfo.clear(corpNo);
            }

            sess.setProperty(Application.token, guidFixStr());
            sess.setProperty(Application.userId, dsUser.getString("ID_"));
            sess.setProperty(Application.bookNo, dsUser.getString("CorpNo_"));
            sess.setProperty(Application.userCode, dsUser.getString("Code_"));
            if (dsUser.getBoolean("DiyRole_")) {
                sess.setProperty(Application.roleCode, dsUser.getString("Code_"));
            } else {
                sess.setProperty(Application.roleCode, dsUser.getString("RoleCode_"));
            }

            // 更新当前用户总数
            updateCurrentUser(device_name, headIn.getString("Screen_"), headIn.getString("Language_"));

            try (MemoryBuffer Buff = new MemoryBuffer(BufferType.getSessionInfo,
                    (String) getProperty(Application.userId), deviceId)) {
                Buff.setField("UserID_", getProperty(Application.userId));
                Buff.setField("UserCode_", getUserCode());
                Buff.setField("UserName_", getUserName());
                Buff.setField("LoginTime_", sess.getProperty(Application.loginTime));
                Buff.setField("YGUser", YGLogin);
                Buff.setField("VerifyMachine", false);
            }
            // 返回值于前台
            getDataOut().getHead().setField("SessionID_", getProperty(Application.token));
            getDataOut().getHead().setField("UserID_", getProperty(Application.userId));
            getDataOut().getHead().setField("UserCode_", getUserCode());
            getDataOut().getHead().setField("CorpNo_", handle.getCorpNo());
            getDataOut().getHead().setField("YGUser", YGLogin);

            // 验证成功，将验证次数赋值为0
            dsUser.edit();
            dsUser.setField("VerifyTimes_", 0);
            dsUser.post();
            tx.commit();
            return true;
        }
    }

    /**
     * 退出系统
     * 
     * @return 暂未使用
     * 
     */
    @Webfunc
    public boolean ExitSystem() {
        if ((String) getProperty(Application.userId) != null) {
            // TODO 此处的key有问题
            MemoryBuffer.delete(BufferType.getSessionInfo, (String) getProperty(Application.userId), "webclient");
        }

        String token = (String) getProperty(Application.token);
        getConnection().execute(String.format("Update %s Set Viability_=-1,LogoutTime_=now() where LoginID_='%s'",
                systemTable.getCurrentUser(), token));
        return true;
    }

    // 获取登录状态
    @Webfunc
    public boolean getState() {
        getDataOut().getHead().setField("UserID_", getProperty(Application.userId));
        getDataOut().getHead().setField("UserCode_", getUserCode());
        getDataOut().getHead().setField("CorpNo_", handle.getCorpNo());
        return true;
    }

    @Override
    public boolean checkSecurity(IHandle handle) {
        return true;
    }

    @Webfunc
    public boolean autoLogin() throws SecurityCheckException {
        Record headIn = getDataIn().getHead();

        String token1 = headIn.getString("token");
        // 加入ABCD是为了仅允许内部调用
        ServerConfig config = ServerConfig.getInstance();
        String token2 = config.getProperty(OssConnection.oss_accessKeySecret, "") + "ABCD";
        // 如果不是内部调用，则返回false
        if (!token2.equals(token1)) {
            return false;
        }

        String clientId = headIn.getString("openid");
        SqlQuery ds = new SqlQuery(this);
        ds.add("SELECT A.Code_,A.Password_ FROM %s A", systemTable.getDeviceVerify());
        ds.add("inner JOIN %s B", systemTable.getUserInfo());
        ds.add("ON A.UserCode_=B.Code_");
        ds.add("WHERE A.MachineCode_='%s' AND A.AutoLogin_=1", clientId);
        ds.open();
        if (ds.eof()) {
            return false;
        }

        headIn.setField("Account_", ds.getString("Code_"));
        headIn.setField("Password_", ds.getString("Password_"));
        headIn.setField("MachineID_", clientId);
        headIn.setField("ClientName_", "Web浏览器");
        headIn.setField("ClientIP_", "127.0.0.1");
        headIn.setField("wx", true);
        return this.Check();
    }

    // 判断手机号码且账号类型为5是否已存在账号
    @Webfunc
    public boolean getTelToUserCode() {
        Record headIn = getDataIn().getHead();
        String userCode = headIn.getString("UserCode_");

        Record headOut = getDataOut().getHead();
        if ("".equals(userCode)) {
            throw new RuntimeException("手机号不允许为空！");
        }

        SqlQuery ds = new SqlQuery(this);
        ds.add("select a.Code_ from %s oi ", systemTable.getBookInfo());
        ds.add("inner join %s a on oi.CorpNo_=a.CorpNo_ and oi.Status_ in(1,2)", systemTable.getUserInfo());
        ds.add("where a.Mobile_='%s' and ((a.BelongAccount_ is null) or (a.BelongAccount_=''))", userCode);
        ds.open();
        if (ds.size() == 0) {
            throw new RuntimeException("您的手机号码不存在于系统中，如果您需要注册帐号，请 <a href='TFrmContact'>联系客服</a> 进行咨询");
        }

        if (ds.size() != 1) {
            throw new RuntimeException(
                    String.format("您的手机绑定了多个帐号，无法登录，建议您使用主账号登录后，在【我的账号--更改我的资料】菜单中设置主附帐号关系后再使用手机号登录！", userCode));
        }
        headOut.setField("UserCode_", ds.getString("Code_"));
        return true;
    }

    // 若返回值为 true，表示已校验，否则表示需要进行认证
    public boolean verifyMachine() throws SecurityCheckException, DataValidateException {
        Record headIn = getDataIn().getHead();

        DataValidateException.stopRun(R.asString(this, "设备ID不允许为空"), !headIn.hasValue("deviceId"));
        String deviceId = headIn.getString("deviceId");

        // 校验帐号的可用状态
        SqlQuery cdsUser = new SqlQuery(this);
        cdsUser.add("select * from %s ", systemTable.getUserInfo());
        cdsUser.add("where Code_='%s' ", getUserCode());
        cdsUser.open();
        DataValidateException.stopRun(String.format(R.asString(this, "没有找到用户帐号 %s"), getUserCode()), cdsUser.eof());
        DataValidateException.stopRun(R.asString(this, "您现登录的帐号已被停止使用，请您联系客服启用后再重新登录"), cdsUser.getInt("Enabled_") < 1);

        // 校验设备码的可用状态
        SqlQuery cdsVer = new SqlQuery(this);
        cdsVer.add("select * from %s", systemTable.getDeviceVerify());
        cdsVer.add("where UserCode_='%s' and MachineCode_='%s'", getUserCode(), deviceId);
        cdsVer.open();
        DataValidateException.stopRun(String.format(R.asString(this, "系统出错(id=%s)，请您重新进入系统"), deviceId), cdsVer.eof());

        if (cdsVer.getInt("Used_") == 1) {
            return true;
        }

        // 未通过则需要检查验证码
        DataValidateException.stopRun(R.asString(this, "验证码不允许为空"), !headIn.hasValue("verifyCode"));
        String verifyCode = headIn.getString("verifyCode");

        if (cdsVer.getInt("Used_") == 2) {
            throw new SecurityCheckException(R.asString(this, "您正在使用的这台设备，被管理员设置为禁止登入系统！"));
        }

        // 更新认证码
        if (!verifyCode.equals(cdsVer.getString("VerifyCode_"))) {
            updateVerifyCode(cdsVer, verifyCode, cdsUser);
        }

        cdsVer.edit();
        cdsVer.setField("Used_", 1);
        cdsVer.setField("FirstTime_", TDateTime.Now());
        cdsVer.post();

        cdsUser.edit();
        cdsUser.setField("VerifyTimes_", 0);
        cdsUser.post();
        return true;
    }

    @Webfunc
    public boolean sendVerifyCode() throws DataValidateException {
        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getObject, getUserCode(), SvrUserLogin.class.getName(),
                "sendVerifyCode")) {
            if (!buff.isNull()) {
                log.info(String.format("verifyCode %s", buff.getString("VerifyCode_")));
                throw new RuntimeException(String.format("请勿在 %d 分钟内重复点击获取认证码！", TimeOut));
            }

            Record headIn = getDataIn().getHead();
            DataValidateException.stopRun("用户帐号不允许为空", "".equals(getUserCode()));

            String deviceId = headIn.getString("deviceId");
            if ("".equals(deviceId)) {
                throw new RuntimeException("认证码不允许为空");
            }

            SqlQuery cdsUser = new SqlQuery(this);
            cdsUser.add("select Mobile_ from %s ", systemTable.getUserInfo());
            cdsUser.add("where Code_='%s' ", getUserCode());
            cdsUser.open();
            DataValidateException.stopRun("系统检测到该帐号还未登记过手机号，无法发送认证码到该手机上，请您联系管理员，让其开一个认证码给您登录系统！", cdsUser.eof());
            String mobile = cdsUser.getString("Mobile_");

            SqlQuery cdsVer = new SqlQuery(this);
            cdsVer.add("select * from %s", systemTable.getDeviceVerify());
            cdsVer.add("where UserCode_='%s' and MachineCode_='%s'", getUserCode(), deviceId);
            cdsVer.open();
            DataValidateException.stopRun("系统出错，请您重新进入系统！", cdsVer.size() != 1);

            String verifyCode = Utils.intToStr(Utils.random(900000) + 100000);
            log.info("{} verifyCode is {}", mobile, verifyCode);

            cdsVer.edit();
            cdsVer.setField("VerifyCode_", verifyCode);
            cdsVer.setField("DeadLine_", TDateTime.Now().incDay(1));
            cdsVer.post();

            // 发送认证码到手机上
            Record record = getDataOut().getHead();
            LocalService svr = new LocalService(this, "SvrNotifyMachineVerify");
            if (svr.exec("verifyCode", verifyCode, "mobile", mobile)) {
                record.setField("Msg_", String.format("系统已将认证码发送到您尾号为 %s 的手机上，并且该认证码 %d 分钟内有效，请注意查收！",
                        mobile.substring(mobile.length() - 4, mobile.length()), TimeOut));
                buff.setExpires(TimeOut * 60);
                buff.setField("VerifyCode", verifyCode);
            } else {
                record.setField("Msg_", String.format("验证码发送失败，失败原因：%s", svr.getMessage()));
            }
            record.setField("VerifyCode_", verifyCode);
            return true;
        }
    }

    // 获取用户的移动设备信息
    public boolean getMachInfo() throws DataValidateException {
        Record headIn = getDataIn().getHead();
        String userCode = headIn.getString("UserCode_");
        DataValidateException.stopRun("用户帐号不允许为空", "".equals(userCode));

        String corpNo = headIn.getString("CorpNo_");
        DataValidateException.stopRun("用户帐套不允许为空", "".equals(corpNo));

        SqlQuery cdsTmp = new SqlQuery(this);
        cdsTmp.add("select * from %s", systemTable.getDeviceVerify());
        cdsTmp.add("where CorpNo_='%s'and UserCode_='%s'", corpNo, userCode);
        /*
         * FIXME MachineType_代表设备类型，6-iOS、7-Android，用于极光推送 JPushRecord
         * 
         * 黄荣君 2017-06-19
         */
        cdsTmp.add("and Used_=1 and MachineType_ in (6,7)");
        cdsTmp.add("and ifnull(MachineCode_,'')<>''");
        cdsTmp.open();

        getDataOut().appendDataSet(cdsTmp);
        return true;
    }

    public void enrollMachineInfo(String corpNo, String userCode, String deviceId, String deviceName) {
        SqlQuery ds = new SqlQuery(this);
        ds.add("select * from %s", systemTable.getDeviceVerify());
        ds.add("where UserCode_='%s' and MachineCode_='%s'", userCode, deviceId);
        ds.open();
        if (!ds.eof()) {
            return;
        }

        ds.append();
        ds.setField("CorpNo_", corpNo);
        ds.setField("UserCode_", userCode);
        ds.setField("VerifyCode_", Utils.intToStr(Utils.random(900000) + 100000));
        ds.setField("DeadLine_", TDateTime.Now().incDay(1));
        ds.setField("MachineCode_", deviceId);
        if (deviceId.startsWith("i_")) {
            // iOS
            ds.setField("MachineType_", 6);
            ds.setField("MachineName_", ClientType.IOS.toString());
        } else if (deviceId.startsWith("n_")) {
            // Android
            ds.setField("MachineType_", 7);
            ds.setField("MachineName_", ClientType.Android.toString());
        } else {
            // 系统默认
            ds.setField("MachineType_", 0);
            ds.setField("MachineName_", deviceName);
        }
        ds.setField("Remark_", "");
        ds.setField("Used_", 0);
        ds.setField("UpdateUser_", userCode);
        ds.setField("UpdateDate_", TDateTime.Now());
        ds.setField("AppUser_", userCode);
        ds.setField("AppDate_", TDateTime.Now());
        ds.setField("UpdateKey_", Utils.newGuid());
        ds.post();
    }

    private boolean isStopUsed(String userCode, String deviceId) {
        SqlQuery ds = new SqlQuery(this);
        ds.add("select * from %s ", systemTable.getDeviceVerify());
        ds.add("where UserCode_='%s' and MachineCode_='%s' ", userCode, deviceId);
        ds.open();
        ds.edit();
        ds.setField("LastTime_", TDateTime.Now());
        ds.post();
        if (ds.getInt("Used_") == 2) {
            return false;
        }
        return true;
    }

    private String guidFixStr() {
        String guid = Utils.newGuid();
        String str = guid.substring(1, guid.length() - 1);
        return str.replaceAll("-", "");
    }

    private boolean isAutoLogin(String userCode, String deviceId) {
        BuildQuery bs = new BuildQuery(this);
        bs.byField("MachineCode_", deviceId);
        bs.byField("Used_", true);
        bs.byField("UserCode_", userCode);
        bs.add("select * from %s", systemTable.getDeviceVerify());
        DataSet ds = bs.open();
        if (!ds.eof()) {
            return ds.getBoolean("AutoLogin_");
        } else {
            return false;
        }
    }

    private void updateVerifyCode(SqlQuery dataVer, String verifyCode, SqlQuery cdsUser) {
        SqlQuery cdsVer = new SqlQuery(this);
        cdsVer.add("select * from %s", systemTable.getDeviceVerify());
        cdsVer.add("where VerifyCode_='%s'", verifyCode);
        cdsVer.open();

        if (cdsVer.eof()) {
            cdsUser.edit();
            // 停用帐号
            if (cdsUser.getInt("VerifyTimes_") == 6) {
                cdsUser.setField("Enabled_", 0);
                cdsUser.post();
                throw new RuntimeException(R.asString(this, "您输入验证码的错误次数已超出规定次数，现账号已被自动停用，若需启用，请您联系客服处理"));
            } else {
                cdsUser.setField("VerifyTimes_", cdsUser.getInt("VerifyTimes_") + 1);
                cdsUser.post();
                throw new RuntimeException(String.format(R.asString(this, "没有找到验证码 %s"), verifyCode));
            }
        }

        String machineCode = cdsVer.getString("MachineCode_");
        if (machineCode == null || "".equals(machineCode)) {
            // 先将此验证码的认证记录删除
            cdsVer.delete();

            // 再将该认证码替换掉之前自动生成的认证码
            dataVer.edit();
            dataVer.setField("VerifyCode_", verifyCode);
            dataVer.post();
        } else {
            throw new RuntimeException("您输入的验证码有误，请重新输入！");
        }
    }

    public void updateCurrentUser(String computer, String screen, String language) {
        getConnection().execute(String.format("Update %s Set Viability_=0 Where Viability_>0 and LogoutTime_<'%s'",
                systemTable.getCurrentUser(), TDateTime.Now().incHour(-1)));
        String SQLCmd = String.format(
                "update %s set Viability_=-1,LogoutTime_='%s' where Account_='%s' and Viability_>-1",
                systemTable.getCurrentUser(), TDateTime.Now(), getUserCode());
        getConnection().execute(SQLCmd);

        // 增加新的记录
        Record rs = new Record();
        rs.setField("UserID_", this.getProperty(Application.userId));
        rs.setField("CorpNo_", handle.getCorpNo());
        rs.setField("Account_", getUserCode());
        rs.setField("LoginID_", this.getProperty(Application.token));
        rs.setField("Computer_", computer);
        rs.setField("clientIP_", this.getProperty(Application.clientIP));
        rs.setField("LoginTime_", TDateTime.Now());
        rs.setField("ParamValue_", handle.getCorpNo());
        rs.setField("KeyCardID_", GuidNull);
        rs.setField("Viability_", Utils.intToStr(Max_Viability));
        rs.setField("LoginServer_", ServerConfig.getAppName());
        rs.setField("Screen_", screen);
        rs.setField("Language_", language);
        SqlOperator opear = new SqlOperator(this);
        opear.setTableName(systemTable.getCurrentUser());
        opear.insert(rs);
    }

}
