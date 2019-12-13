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

        getDataOut().appendDataSet(cdsTmp);
        return true;
    }

    /**
     * 具体使用参考 UserList
     * <p>
     * 加载指定帐套的所有用户
     */
    public boolean loadList() throws DataValidateException {
        Record headIn = getDataIn().getHead();
        DataValidateException.stopRun("CorpNo_ 不允许为空", !headIn.hasValue("CorpNo_"));
        String corpNo = Utils.safeString(headIn.getString("CorpNo_"));

        SqlQuery cdsTmp = new SqlQuery(handle);
        cdsTmp.add("select ID_,CorpNo_,Code_,Name_,QQ_,Mobile_,SuperUser_,");
        cdsTmp.add("LastRemindDate_,EmailAddress_,RoleCode_,ProxyUsers_,Enabled_,DiyRole_");
        cdsTmp.add("from %s ", systemTable.getUserInfo());
        cdsTmp.add("where CorpNo_='%s'", corpNo);
        cdsTmp.open();

        DataValidateException.stopRun(String.format("帐套 %s 没有任何用户信息", corpNo), cdsTmp.eof());
        getDataOut().appendDataSet(cdsTmp);
        return true;
    }

}
