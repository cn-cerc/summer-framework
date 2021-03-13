package cn.cerc.mis.services;

import cn.cerc.core.Record;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.core.CustomService;

public class SvrBookInfo extends CustomService {

    public boolean getRecord() {
        String corpNo = getDataIn().getHead().getString("corpNo");
        SqlQuery ds = new SqlQuery(handle);
        ds.add("select CorpNo_,Type_,ShortName_,Name_,Address_,Tel_,Status_,Industry_,FastTel_,Currency_,");
        ds.add("ManagerPhone_,StartHost_,Contact_,Authentication_,CorpMailbox_,Fax_");
        ds.add("from %s where CorpNo_=N'%s'", systemTable.getBookInfo(), corpNo);
        ds.open();
        if (ds.eof()) {
            return false;
        }

        Record headOut = getDataOut().getHead();
        headOut.setField("CorpNo_", ds.getString("CorpNo_"));
        headOut.setField("ShortName_", ds.getString("ShortName_"));
        headOut.setField("Name_", ds.getString("Name_"));
        headOut.setField("Address_", ds.getString("Address_"));
        headOut.setField("Tel_", ds.getString("Tel_"));
        headOut.setField("FastTel_", ds.getString("FastTel_"));
        headOut.setField("ManagerPhone_", ds.getString("ManagerPhone_"));
        headOut.setField("StartHost_", ds.getString("StartHost_"));
        headOut.setField("Contact_", ds.getString("Contact_"));
        headOut.setField("Authentication_", ds.getString("Authentication_"));
        headOut.setField("Industry_", ds.getString("Industry_"));
        headOut.setField("Status_", ds.getInt("Status_"));
        headOut.setField("Type_", ds.getInt("Type_"));
        headOut.setField("Currency_", ds.getString("Currency_"));
        headOut.setField("Email_", ds.getString("CorpMailbox_"));
        headOut.setField("Fax_", ds.getString("Fax_"));
        return true;
    }
}
