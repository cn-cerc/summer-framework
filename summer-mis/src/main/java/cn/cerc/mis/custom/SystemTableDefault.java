package cn.cerc.mis.custom;

import org.springframework.stereotype.Component;

import cn.cerc.mis.core.ISystemTable;

@Component
public class SystemTableDefault implements ISystemTable {

    @Override
    public String getBookInfo() {
        return "s_book_info";
    }

    @Override
    public String getBookOptions() {
        return "s_book_options";
    }

    @Override
    public String getAppMenus() {
        return "s_app_menus";
    }

    @Override
    public String getCustomMenus() {
        return "s_custom_menus";
    }

    @Override
    public String getUserMenus() {
        return "s_user_menus";
    }

    @Override
    public String getUserInfo() {
        return "s_user_info";
    }

    @Override
    public String getUserOptions() {
        return "s_user_options";
    }

    @Override
    public String getUserRoles() {
        return "s_user_roles";
    }

    @Override
    public String getRoleAccess() {
        return "s_role_access";
    }

    @Override
    public String getDeviceVerify() {
        return "s_device_verify";
    }

    @Override
    public String getSecurityMobile() {
        return "s_security_mobile";
    }

    @Override
    public String getCurrentUser() {
        return "s_current_user";
    }

    @Override
    public String getUserMessages() {
        return "s_user_messages";
    }

    @Override
    public String getUserLogs() {
        return "s_user_logs";
    }

    @Override
    public String getAppLogs() {
        return "s_app_logs";
    }

    @Override
    public String getPageLogs() {
        return "s_page_logs";
    }

    @Override
    public String getOnlineUsers() {
        return "s_online_users";
    }

    @Override
    public String getLangDict() {
        return "s_lang_dict";
    }

    @Override
    public String getLanguage() {
        return "s_dict_language";
    }

    @Override
    public String getManageBook() {
        return "000000";
    }

}
