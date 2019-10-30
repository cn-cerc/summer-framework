package cn.cerc.mis.page.qrcode;

import cn.cerc.mis.core.AbstractForm;
import cn.cerc.mis.core.ClientDevice;
import cn.cerc.mis.core.IPage;
import cn.cerc.mis.page.service.SvrAutoLogin;

public class FrmQRCode extends AbstractForm implements JayunEasyLogin {

    @Override
    public IPage execute() throws Exception {
        new JayunQrcode(this.getRequest(), this.getResponse()).execute(this);
        return null;
    }

    @Override
    public JayunMessage getLoginToken() {
        JayunMessage message;
        SvrAutoLogin svrLogin = new SvrAutoLogin(this);
        if (svrLogin.check(this, this.getRequest())) {
            ClientDevice info = ((ClientDevice) this.getClient());
            message = new JayunMessage(true, "已确认");
            message.setToken(info.getSid());
            message.setUrl("WebDefault");
        } else {
            message = new JayunMessage(false, svrLogin.getMessage());
        }
        return message;
    }

    @Override
    public String getNotifyUrl() {
        return "FrmSecurity";
    }

    @Override
    public boolean logon() {
        return true;
    }

}
