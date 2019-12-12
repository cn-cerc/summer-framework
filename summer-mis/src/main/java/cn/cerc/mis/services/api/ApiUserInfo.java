package cn.cerc.mis.services.api;

import cn.cerc.core.Record;
import cn.cerc.core.Utils;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.CustomService;
import cn.cerc.mis.core.DataValidateException;
import cn.cerc.mis.core.ISystemTable;

public class ApiUserInfo extends CustomService {

    public boolean getUserInfo() throws DataValidateException {
        Record headIn = getDataIn().getHead();
        DataValidateException.stopRun("UserCode_ 不允许为空", !headIn.hasValue("UserCode_"));
        String userCode = Utils.safeString(headIn.getString("UserCode_"));

        ISystemTable sysTable = Application.getBean("systemTable", ISystemTable.class);
        SqlQuery cdsTmp = new SqlQuery(this);
        cdsTmp.add("select a.Code_,a.Enabled_,a.Name_,a.SuperUser_,a.DiyRole_,a.RoleCode_,oi.Type_,a.ImageUrl_ ");
        cdsTmp.add("from %s a ", sysTable.getUserInfo());
        cdsTmp.add("inner join %s oi on a.CorpNo_=oi.CorpNo_ ", sysTable.getBookInfo());
        cdsTmp.add("where a.Code_='%s'", userCode);
        cdsTmp.open();
        DataValidateException.stopRun(String.format("用户代码 %s 不存在!", userCode), cdsTmp.eof());

        return true;
    }

}
