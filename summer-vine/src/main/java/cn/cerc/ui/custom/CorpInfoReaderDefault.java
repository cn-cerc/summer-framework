package cn.cerc.ui.custom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.core.ISystemTable;
import cn.cerc.mis.services.ICorpInfoReader;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CorpInfoReaderDefault implements ICorpInfoReader, IHandle {
    @Autowired
    private ISystemTable systemTable;
    private ISession session;

    @Override
    public Record getCorpInfo(String corpNo) {
        SqlQuery ds = new SqlQuery(session);
        ds.add("select CorpNo_,Type_,ShortName_,Name_,Address_,Tel_,Status_,Industry_,FastTel_,Currency_,");
        ds.add("ManagerPhone_,StartHost_,Contact_,Authentication_,CorpMailbox_,Fax_");
        ds.add("from %s where CorpNo_=N'%s'", systemTable.getBookInfo(), corpNo);
        ds.open();
        if (ds.eof()) {
            return null;
        }

        Record result = new Record();
        result.setField("CorpNo_", ds.getString("CorpNo_"));
        result.setField("ShortName_", ds.getString("ShortName_"));
        result.setField("Name_", ds.getString("Name_"));
        result.setField("Address_", ds.getString("Address_"));
        result.setField("Tel_", ds.getString("Tel_"));
        result.setField("FastTel_", ds.getString("FastTel_"));
        result.setField("ManagerPhone_", ds.getString("ManagerPhone_"));
        result.setField("StartHost_", ds.getString("StartHost_"));
        result.setField("Contact_", ds.getString("Contact_"));
        result.setField("Authentication_", ds.getString("Authentication_"));
        result.setField("Industry_", ds.getString("Industry_"));
        result.setField("Status_", ds.getInt("Status_"));
        result.setField("Type_", ds.getInt("Type_"));
        result.setField("Currency_", ds.getString("Currency_"));
        result.setField("Email_", ds.getString("CorpMailbox_"));
        result.setField("Fax_", ds.getString("Fax_"));
        return result;
    }

    @Override
    public ISession getSession() {
        return session;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
    }

}
