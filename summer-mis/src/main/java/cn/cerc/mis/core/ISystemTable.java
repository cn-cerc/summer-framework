package cn.cerc.mis.core;

public interface ISystemTable {
    // 帐套资料表
    public String getBookInfo(); // "OurInfo";
    // 帐套参数档

    public String getBookOptions(); // "VineOptions";
    // 应用菜单表

    public String getAppMenus(); // "SysFormDef";
    // 客户客制化菜单

    public String getCustomMenus(); // "cusmenu";
    // 用户自定义菜单

    public String getUserMenus(); // "UserMenu";

    // 用户资料表
    public String getUserInfo(); // "Account";
    // 用户参数表

    public String getUserOptions(); // "UserOptions";
    // 用户角色表

    public String getUserRoles(); // "UserRoles";
    // 角色权限表

    public String getRoleAccess(); // "UserAccess";
    // 用户设备认证记录表

    public String getDeviceVerify(); // "AccountVerify";
    // 安全手机管控表

    public String getSecurityMobile(); // "s_securityMobile";

    // 当前在线用户
    public String getCurrentUser(); // "CurrentUser";
    // 记录用户需要查看的消息

    public String getUserMessages(); // "message_temp";
    // 记录用户的关键操作

    public String getUserLogs(); // "UserLogs";
    // 记录应用服务被调用的历史

    public String getAppLogs(); // "AppServiceLogs";
    // 记录网页被调用的历史

    public String getPageLogs(); // "WebPageLogs";
    // 记录在线用户数

    public String getOnlineUsers(); // "onlineusers";

    // 运营商帐套代码
    public String getManageBook(); // "000000";

    // 多语言数据字典: 旧版本
    public String getLangDict(); // "s_langDict";

    // 多语言数据字典: 新版本
    public String getLanguage(); // "s_language";
//
//    public static String get(String tableCode) {
//        return Application.getAppConfig().getParam(tableCode, tableCode);
//    }
}