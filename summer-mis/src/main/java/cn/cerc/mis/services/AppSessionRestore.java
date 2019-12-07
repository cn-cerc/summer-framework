package cn.cerc.mis.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.Record;
import cn.cerc.core.TDateTime;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.CustomService;
import cn.cerc.mis.core.DataValidateException;
import cn.cerc.mis.core.ServiceException;
import cn.cerc.mis.other.UserNotFindException;
import cn.cerc.mis.core.HandleDefault;

public class AppSessionRestore extends CustomService {
    private static final Logger log = LoggerFactory.getLogger(AppSessionRestore.class);

    public boolean byUserCode() throws ServiceException, UserNotFindException {
        Record headIn = getDataIn().getHead();
        DataValidateException.stopRun("用户id不允许为空", !headIn.hasValue("userCode"));
        String userCode = headIn.getString("userCode");

        SqlQuery cdsUser = new SqlQuery(this);
        cdsUser.add("select ID_,Code_,RoleCode_,DiyRole_,CorpNo_, Name_ as UserName_,ProxyUsers_");
        cdsUser.add("from %s ", systemTable.getUserInfo());
        cdsUser.add("where Code_= '%s' ", userCode);
        cdsUser.open();
        if (cdsUser.eof()) {
            throw new UserNotFindException(userCode);
        }

        Record headOut = getDataOut().getHead();
        headOut.setField("LoginTime_", TDateTime.Now());
        copyData(cdsUser, headOut);
        return true;
    }

    public boolean byToken() throws ServiceException {
        Record headIn = getDataIn().getHead();
        DataValidateException.stopRun("token不允许为空", !headIn.hasValue("token"));
        String token = headIn.getString("token");

        SqlQuery cdsCurrent = new SqlQuery(this);
        cdsCurrent.add("select CorpNo_,UserID_,LoginTime_,Account_ as UserCode_,Language_ ");
        cdsCurrent.add("from %s", systemTable.getCurrentUser());
        cdsCurrent.add("where loginID_= '%s' ", token);
        cdsCurrent.open();
        if (cdsCurrent.eof()) {
            log.warn(String.format("token %s 没有找到！", token));
            HandleDefault sess = (HandleDefault) this.getProperty(null);
            sess.setProperty(Application.token, null);
            return false;
        }
        String userId = cdsCurrent.getString("UserID_");

        SqlQuery cdsUser = new SqlQuery(this);
        cdsUser.add("select ID_,Code_,DiyRole_,RoleCode_,CorpNo_, Name_ as UserName_,ProxyUsers_");
        cdsUser.add("from %s", systemTable.getUserInfo(), userId);
        cdsUser.add("where ID_='%s'", userId);
        cdsUser.open();
        if (cdsUser.eof()) {
            log.warn(String.format("userId %s 没有找到！", userId));
            HandleDefault sess = (HandleDefault) this.getProperty(null);
            sess.setProperty(Application.token, null);
            return false;
        }

        Record headOut = getDataOut().getHead();
        headOut.setField("LoginTime_", cdsCurrent.getDateTime("LoginTime_"));
        headOut.setField("Language_", cdsCurrent.getString("Language_"));
        copyData(cdsUser, headOut);
        return true;
    }

    private void copyData(SqlQuery ds, Record headOut) {
        headOut.setField("UserID_", ds.getString("ID_"));
        headOut.setField("UserCode_", ds.getString("Code_"));
        headOut.setField("UserName_", ds.getString("UserName_"));
        headOut.setField("CorpNo_", ds.getString("CorpNo_"));
        if (ds.getBoolean("DiyRole_"))
            headOut.setField("RoleCode_", ds.getString("Code_"));
        else
            headOut.setField("RoleCode_", ds.getString("RoleCode_"));
        headOut.setField("ProxyUsers_", ds.getString("ProxyUsers_"));
    }

}
