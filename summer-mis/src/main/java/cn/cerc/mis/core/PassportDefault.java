package cn.cerc.mis.core;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.cerc.core.IHandle;
import cn.cerc.mis.rds.PassportRecord;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PassportDefault implements IPassport {
    private IHandle handle;

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

    @Override
    public void setHandle(IHandle handle) {
        this.handle = handle;
    }

    public IHandle getHandle() {
        return handle;
    }

}