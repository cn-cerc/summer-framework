package cn.cerc.mis.core;

import cn.cerc.core.IHandle;
import cn.cerc.mis.rds.PassportRecord;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Deprecated
//TODO PassportDefault 此对象不应该存在框架中
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

    public IHandle getHandle() {
        return handle;
    }

    @Override
    public void setHandle(IHandle handle) {
        this.handle = handle;
    }

}