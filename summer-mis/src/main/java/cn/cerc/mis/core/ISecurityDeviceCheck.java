package cn.cerc.mis.core;

import cn.cerc.db.core.ISessionOwner;

public interface ISecurityDeviceCheck extends ISessionOwner {

    PassportResult pass(IForm form);
}
