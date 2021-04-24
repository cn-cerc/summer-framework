package cn.cerc.mis.core;

import cn.cerc.db.core.IHandle;
import cn.cerc.mis.rds.PassportRecord;

public interface IPassport extends IHandle {

    // 是否有菜单的执行权限
    default boolean pass(IForm form) {
        String securityCheck = form.getParam("security", "true");
        if (!"true".equals(securityCheck)) {
            return true;
        }
        String verList = form.getParam("verlist", null);
        String procCode = form.getPermission();
        return passProc(verList, procCode);
    }

    // 是否有程序的执行权限
    boolean passProc(String versions, String procCode);

    // 是否有程序指定动作的权限
    boolean passAction(String procCode, String action);

    // 是否有菜单的执行权限
    boolean passsMenu(String menuCode);

    // 返回指定程序的权限记录
    PassportRecord getRecord(String procCode);
}
