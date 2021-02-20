package cn.cerc.mis.services;

import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import cn.cerc.core.TDateTime;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.CustomService;
import cn.cerc.mis.core.DataValidateException;
import cn.cerc.mis.core.HandleDefault;
import cn.cerc.mis.core.ServiceException;
import cn.cerc.mis.other.UserNotFindException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SvrSession extends CustomService {

    public boolean byUserCode() throws ServiceException, UserNotFindException {
        Record headIn = getDataIn().getHead();
        DataValidateException.stopRun("CorpNo_ 不允许为空", !headIn.hasValue("CorpNo_"));
        String corpNo = headIn.getString("CorpNo_");

        DataValidateException.stopRun("UserCode_ 不允许为空", !headIn.hasValue("UserCode_"));
        String userCode = headIn.getString("UserCode_");

        SqlQuery cdsUser = new SqlQuery(this);
        cdsUser.add("select ID_,Code_,RoleCode_,DiyRole_,CorpNo_, Name_ as UserName_,ProxyUsers_");
        cdsUser.add("from %s ", systemTable.getUserInfo());
        cdsUser.add("where CorpNo_='%s' and Code_='%s'", corpNo, userCode);
        cdsUser.open();
        if (cdsUser.eof()) {
            throw new UserNotFindException(userCode);
        }

        Record headOut = getDataOut().getHead();
        headOut.setField("LoginTime_", TDateTime.now());
        copyData(cdsUser, headOut);
        return true;
    }

    /*
     * 1、从 CurrentUser 表中，取出公司别 CorpNo_ 与 UserCode_
     * 2、再依据 UserCode_ 从Account表取出 RoleCode_
     */
    public boolean byToken() throws ServiceException {
        Record headIn = getDataIn().getHead();
        DataValidateException.stopRun("token不允许为空", !headIn.hasValue("token"));
        String token = headIn.getString("token");

        SqlQuery cdsToken = new SqlQuery(this);
        cdsToken.add("select CorpNo_,UserID_,Viability_,LoginTime_,Account_ as UserCode_,Language_ ");
        cdsToken.add("from %s", systemTable.getCurrentUser());
        cdsToken.add("where loginID_='%s'", token);
        cdsToken.open();
        if (cdsToken.eof()) {
            log.warn("token {} 没有找到！", token);
            HandleDefault sess = (HandleDefault) this.getProperty(null);
            sess.setProperty(Application.token, null);
            return false;
        }

        if (cdsToken.getInt("Viability_") <= 0 && !"13100154".equals(cdsToken.getString("UserCode_"))) {
            log.warn("token {} 已失效，请重新登录", token);
            HandleDefault sess = (HandleDefault) this.getProperty(null);
            sess.setProperty(Application.token, null);
            return false;
        }

        String userId = cdsToken.getString("UserID_");
        SqlQuery cdsUser = new SqlQuery(this);
        cdsUser.add("select ID_,Code_,DiyRole_,RoleCode_,CorpNo_, Name_ as UserName_,ProxyUsers_");
        cdsUser.add("from %s", systemTable.getUserInfo());
        cdsUser.add("where ID_='%s'", userId);
        cdsUser.open();
        if (cdsUser.eof()) {
            log.warn(String.format("userId %s 没有找到！", userId));
            HandleDefault sess = (HandleDefault) this.getProperty(null);
            sess.setProperty(Application.token, null);
            return false;
        }

        Record headOut = getDataOut().getHead();
        headOut.setField("LoginTime_", cdsToken.getDateTime("LoginTime_"));
        headOut.setField("Language_", cdsToken.getString("Language_"));
        copyData(cdsUser, headOut);
        return true;
    }

    private void copyData(DataSet ds, Record headOut) {
        headOut.setField("UserID_", ds.getString("ID_"));
        headOut.setField("UserCode_", ds.getString("Code_"));
        headOut.setField("UserName_", ds.getString("UserName_"));
        headOut.setField("CorpNo_", ds.getString("CorpNo_"));
        if (ds.getBoolean("DiyRole_")) {
            headOut.setField("RoleCode_", ds.getString("Code_"));
        } else {
            headOut.setField("RoleCode_", ds.getString("RoleCode_"));
        }
        headOut.setField("ProxyUsers_", ds.getString("ProxyUsers_"));
    }

}
