package cn.cerc.mis.core;

public interface ISystemTable {

    // 帐套资料表
    String getBookInfo(); // "OurInfo";

    // 帐套参数档
    String getBookOptions(); // "VineOptions";

    // 应用菜单表
    String getAppMenus(); // "s_menus";

    // 客户客制化菜单
    @Deprecated
    String getCustomMenus(); // "cusmenu";

    // 用户自定义菜单
    String getUserMenus(); // "UserMenu";

    // 用户资料表
    String getUserInfo(); // "Account";

    // 用户参数表
    String getUserOptions(); // "UserOptions";

    // 用户角色表
    String getUserRoles(); // "UserRoles";

    // 角色权限表
    String getRoleAccess(); // "UserAccess";

    // 用户设备认证记录表
    String getDeviceVerify(); // "AccountVerify";

    // 安全手机管控表
    @Deprecated
    String getSecurityMobile(); // "s_securityMobile";

    // 当前在线用户
    String getCurrentUser(); // "CurrentUser";

    // 记录用户需要查看的消息
    String getUserMessages(); // "message_temp";

    // 记录用户的关键操作
    String getUserLogs(); // "UserLogs";

    // 记录应用服务被调用的历史
    String getAppLogs(); // "AppServiceLogs";

    // 记录网页被调用的历史
    @Deprecated
    String getPageLogs(); // "WebPageLogs";

    // 记录在线用户数
    String getOnlineUsers(); // "onlineusers";

    // 运营商帐套代码
    String getManageBook(); // "000000";

    // 多语言数据字典: 旧版本
    @Deprecated
    String getLangDict(); // "s_langDict";

    // 多语言数据字典: 新版本
    String getLanguage(); // "s_language";

}