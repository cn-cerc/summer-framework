package cn.cerc.mis.custom;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.cerc.core.ISession;
import cn.cerc.mis.core.IPassport;
import cn.cerc.mis.rds.PassportRecord;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PassportDefault implements IPassport {
    private ISession session;
    
    private static final String GUEST_DEFAULT = "guest.default";

    @Override
    public boolean passProc(String versions, String procCode) {
        return GUEST_DEFAULT.equals(procCode);
    }

    @Override
    public boolean passAction(String procCode, String action) {
        return GUEST_DEFAULT.equals(procCode);
    }

    @Override
    public PassportRecord getRecord(String procCode) {
        PassportRecord result = new PassportRecord();
        result.setAdmin(GUEST_DEFAULT.equals(procCode));
        return result;
    }

    @Override
    public boolean passsMenu(String menuCode) {
        return true;
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