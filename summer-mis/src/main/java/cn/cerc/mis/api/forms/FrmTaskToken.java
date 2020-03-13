package cn.cerc.mis.api.forms;

import cn.cerc.core.TDateTime;
import cn.cerc.core.Utils;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.config.ApplicationConfig;
import cn.cerc.mis.core.AbstractForm;
import cn.cerc.mis.core.IPage;
import cn.cerc.mis.language.Language;
import cn.cerc.mis.page.JsonPage;
import cn.cerc.mis.rds.StubHandle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FrmTaskToken extends AbstractForm {

    @Override
    public IPage execute() {
        // 返回当前主机信息
        return new JsonPage(this).setResultMessage(true, ServerConfig.getAppName() + ":" + TDateTime.Now());
    }

    public IPage register() {
        JsonPage jsonPage = new JsonPage(this);

        if (ApplicationConfig.isReplica()) {
            return jsonPage.setResultMessage(false, "replica 主机不支持注册 token");
        }

        // 用户代码
        String userCode = getRequest().getParameter("userCode");
        if (Utils.isEmpty(userCode)) {
            return jsonPage.setResultMessage(false, "userCode 不允许为空");
        }
        if (!StubHandle.DefaultUser.equals(userCode)) {
            return jsonPage.setResultMessage(false, "userCode 不是系统专用帐号");
        }

        // 令牌代码
        String token = getRequest().getParameter("token");
        if (Utils.isEmpty(token)) {
            return jsonPage.setResultMessage(false, "token 不允许为空");
        }

        // 主机名称
        String machine = getRequest().getParameter("machine");
        if (Utils.isEmpty(machine)) {
            return jsonPage.setResultMessage(false, "machine 不允许为空");
        }

        // 访问地址
        String clientIP = Utils.getIP(getRequest());

        SqlQuery cdsUser = new SqlQuery(this);
        cdsUser.add("select ID_,Code_,RoleCode_,DiyRole_,CorpNo_, Name_ as UserName_,ProxyUsers_");
        cdsUser.add("from %s ", systemTable.getUserInfo());
        cdsUser.add("where Code_='%s'", userCode);
        cdsUser.open();
        if (cdsUser.eof()) {
            return jsonPage.setResultMessage(false, "用户帐号不存在");
        }

        SqlQuery cdsToken = new SqlQuery(this);
        cdsToken.add("select * from %s", systemTable.getCurrentUser());
        cdsToken.add("where loginID_='%s'", token);
        cdsToken.open();
        if (cdsToken.eof()) {
            cdsToken.append();
            cdsToken.setField("UserID_", cdsUser.getString("ID_"));
            cdsToken.setField("CorpNo_", StubHandle.DefaultBook);
            cdsToken.setField("Account_", StubHandle.DefaultUser);
            cdsToken.setField("LoginID_", token);
            cdsToken.setField("Computer_", machine);
            cdsToken.setField("clientIP_", clientIP);
            cdsToken.setField("LoginTime_", TDateTime.Now());
            cdsToken.setField("ParamValue_", StubHandle.DefaultBook);
            cdsToken.setField("KeyCardID_", "");
            cdsToken.setField("Viability_", 1);
            cdsToken.setField("LoginServer_", machine);
            cdsToken.setField("Screen_", "");
            cdsToken.setField("Language_", Language.zh_CN);
            cdsToken.post();
            log.warn("userCode {} token {} 注册到mysql", userCode, token);
        }
        return jsonPage.setResultMessage(true, "token 注册成功");
    }

    @Override
    public boolean passDevice() {
        return true;
    }

}
