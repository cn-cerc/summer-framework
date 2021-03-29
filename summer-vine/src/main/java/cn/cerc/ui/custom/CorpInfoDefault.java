package cn.cerc.ui.custom;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;
import cn.cerc.mis.core.Handle;
import cn.cerc.mis.services.BookInfoRecord;
import cn.cerc.mis.services.MemoryBookInfo;
import cn.cerc.ui.core.ICorpInfo;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CorpInfoDefault implements ICorpInfo {

    private ISession session;

    @Override
    public ISession getSession() {
        return session;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
    }

    @Override
    public String getShortName() {
        IHandle handle = new Handle(session);
        BookInfoRecord item = MemoryBookInfo.get(handle, handle.getCorpNo());
        return item.getShortName();
    }

}
