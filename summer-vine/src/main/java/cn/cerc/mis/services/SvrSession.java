package cn.cerc.mis.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ClassResource;
import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import cn.cerc.core.TDateTime;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.CustomService;
import cn.cerc.mis.core.DataValidateException;
import cn.cerc.mis.core.ServiceException;
import cn.cerc.mis.other.UserNotFindException;

public class SvrSession extends CustomService {
    private static final Logger log = LoggerFactory.getLogger(SvrSession.class);
    private static final ClassResource res = new ClassResource(SvrSession.class, SummerMIS.ID);

    public boolean byUserCode() throws ServiceException, UserNotFindException {
        Record headIn = getDataIn().getHead();
        DataValidateException.stopRun(String.format(res.getString(1, "%s 不允许为空"), "CorpNo_"),
                !headIn.hasValue("CorpNo_"));
        String corpNo = headIn.getString("CorpNo_");

        DataValidateException.stopRun(String.format(res.getString(1, "%s 不允许为空"), "UserCode_"),
                !headIn.hasValue("UserCode_"));
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
     * 1、从 CurrentUser 表中，取出公司别 CorpNo_ 与 UserCode_ 2、再依据 UserCode_ 从Account表取出
     * RoleCode_
     */
    public boolean byToken() throws ServiceException {
        Record headIn = getDataIn().getHead();
        DataValidateException.stopRun(String.format(res.getString(1, "%s 不允许为空"), "token"), !headIn.hasValue("token"));
        String token = headIn.getString("token");

        SqlQuery cdsToken = new SqlQuery(this);
        cdsToken.add("select CorpNo_,UserID_,Viability_,LoginTime_,Account_ as UserCode_,Language_ ");
        cdsToken.add("from %s", systemTable.getCurrentUser());
        cdsToken.add("where loginID_='%s'", token);
        cdsToken.open();
        if (cdsToken.eof()) {
            log.warn("can not find token in database: {}", token);
            this.getSession().setProperty(Application.TOKEN, null);
            return false;
        }

        if (cdsToken.getInt("Viability_") <= 0 && !"13100154".equals(cdsToken.getString("UserCode_"))) {
            log.warn("token expired，please login again {}", token);
            this.getSession().setProperty(Application.TOKEN, null);
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
            this.getSession().setProperty(Application.TOKEN, null);
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
