package cn.cerc.mis.custom;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;
import cn.cerc.mis.core.IPassport;
import cn.cerc.mis.rds.PassportRecord;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Deprecated
//TODO PassportDefault 此对象不应该存在框架中
public class PassportDefault implements IPassport {
    private IHandle handle;
    private ISession session;

    @Override
    public boolean passProc(String versions, String procCode) {
        return true;
    }

    @Override
    public boolean passAction(String procCode, String action) {
        return true;
    }

    @Override
    public PassportRecord getRecord(String procCode) {
        PassportRecord result = new PassportRecord();
        result.setAdmin(true);
        return result;
    }

    @Override
    public boolean passsMenu(String menuCode) {
        return true;
    }

    public IHandle getHandle() {
        return handle;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
    }

    @Override
    public ISession getSession() {
        return this.session;
    }

}