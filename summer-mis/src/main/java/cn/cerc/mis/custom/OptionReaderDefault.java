package cn.cerc.mis.custom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.cerc.core.ISession;
import cn.cerc.core.Utils;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.core.IOptionReader;
import cn.cerc.mis.core.ISystemTable;

/**
 * 读取帐套参数
 * 
 * @author ZhangGong
 *
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OptionReaderDefault implements IOptionReader, IHandle {
    @Autowired
    private ISystemTable systemTable;
    private ISession session;

    @Override
    public String getCorpValue(String corpNo, String optionKey, String defaultValue) {
        if (Utils.isEmpty(optionKey))
            throw new RuntimeException("corp optionKey is null");

        SqlQuery query = new SqlQuery(this);
        query.add("select Value_ from %s", systemTable.getBookOptions());
        query.add("where CorpNo_='%s'", corpNo);
        query.add("and Code_='%s'", Utils.safeString(optionKey));
        query.open();

        return query.eof() ? defaultValue : query.getString("Value_");
    }
    
    @Override
    public String getUserValue(String userCode, String optionKey, String defaultValue) {
        if (Utils.isEmpty(optionKey))
            throw new RuntimeException("user optionKey is null");

        SqlQuery query = new SqlQuery(this);
        query.add("select Value_ from %s", systemTable.getUserOptions());
        query.add("where UserCode_=N'%s' and Code_=N'%s'", userCode, Utils.safeString(optionKey));
        query.open();

        return query.eof() ? defaultValue : query.getString("Value_");
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
