package cn.cerc.mis.config;

import cn.cerc.mis.core.ISystemTable;

public class SystemTableDefault implements ISystemTable {

    @Override
    public String getBookInfo() {
        return "s_BookInfo";
    }

    @Override
    public String getBookOptions() {
        return "s_BookOptions";
    }

    @Override
    public String getAppMenus() {
        return "s_AppMenus";
    }

    @Override
    public String getCustomMenus() {
        return "s_CustomMenus";
    }

    @Override
    public String getUserMenus() {
        return "s_UserMenus";
    }

    @Override
    public String getUserInfo() {
        return "s_UserInfo";
    }

    @Override
    public String getUserOptions() {
        return "s_UserOptions";
    }

    @Override
    public String getUserRoles() {
        return "s_UserRoles";
    }

    @Override
    public String getRoleAccess() {
        return "s_RoleAccess";
    }

    @Override
    public String getDeviceVerify() {
        return "s_DeviceVerify";
    }

    @Override
    public String getSecurityMobile() {
        return "s_SecurityMobile";
    }

    @Override
    public String getCurrentUser() {
        return "s_CurrentUser";
    }

    @Override
    public String getUserMessages() {
        return "s_UserMessages";
    }

    @Override
    public String getUserLogs() {
        return "s_UserLogs";
    }

    @Override
    public String getAppLogs() {
        return "s_AppLogs";
    }

    @Override
    public String getPageLogs() {
        return "s_PageLogs";
    }

    @Override
    public String getOnlineUsers() {
        return "s_OnlineUsers";
    }

    @Override
    public String getLangDict() {
        return "s_LangDict";
    }

    @Override
    public String getLanguage() {
        return "s_Language";
    }

    @Override
    public String getManageBook() {
        return "000000";
    }
}
