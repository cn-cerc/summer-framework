package cn.cerc.mis.services;

import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.core.AbstractService;
import cn.cerc.mis.core.IStatus;
import cn.cerc.mis.core.ServiceException;

public class SvrUserOption extends AbstractService {

    @Override
    public IStatus execute(DataSet dataIn, DataSet dataOut) throws ServiceException {
        Record head = dataIn.getHead();
        SqlQuery ds = new SqlQuery(this);
        ds.add(String.format("select Value_ from %s", systemTable.getUserOptions()));
        ds.add(String.format("where UserCode_=N'%s' and Code_=N'%s'", this.getUserCode(), head.getString("Code_")));
        ds.open();
        dataOut.appendDataSet(ds);
        return success();
    }
}