package cn.cerc.mis.api.services;

import cn.cerc.core.ClassResource;
import cn.cerc.core.Record;
import cn.cerc.core.Utils;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.core.CustomService;
import cn.cerc.mis.core.DataValidateException;

public class ApiBookOption extends CustomService {
    private static final ClassResource res = new ClassResource(ApiBookOption.class, SummerMIS.ID);

    /**
     * @return 根据帐套代码、参数代码获取参数值
     * @throws DataValidateException 数据校验异常
     */
    public boolean getValue() throws DataValidateException {
        Record headIn = getDataIn().getHead();

        DataValidateException.stopRun(res.getString(1, "帐套代码不允许为空"), !headIn.hasValue("CorpNo_"));
        String corpNo = Utils.safeString(headIn.getString("CorpNo_"));

        DataValidateException.stopRun(res.getString(2, "参数代码不允许为空"), !headIn.hasValue("Code_"));
        String code = Utils.safeString(headIn.getString("Code_"));

        SqlQuery cdsTmp = new SqlQuery(this);
        cdsTmp.add("select Value_ from %s", systemTable.getBookOptions());
        cdsTmp.add("where CorpNo_='%s'", corpNo);
        cdsTmp.add("and Code_='%s'", code);
        cdsTmp.open();

        getDataOut().appendDataSet(cdsTmp);
        return true;
    }

}
