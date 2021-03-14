package cn.cerc.mis.custom;

import org.springframework.stereotype.Component;

import cn.cerc.core.ISession;
import cn.cerc.mis.core.IForm;
import cn.cerc.mis.core.ISecurityDeviceCheck;
import cn.cerc.mis.core.PassportResult;

@Component
public class SecurityDeviceCheckDefault implements ISecurityDeviceCheck {

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
    public PassportResult pass(IForm form) {
        return PassportResult.PASS;
    }

}
