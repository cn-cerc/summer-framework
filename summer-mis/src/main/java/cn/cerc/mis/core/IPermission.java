package cn.cerc.mis.core;

public interface IPermission {

    // 取得权限代码
    String getPermission();

    // 设备安全检查通过否，为true时需要进行进一步授权
    default boolean isSecurityDevice() {
        return passDevice();
    }
    
    @Deprecated
    default boolean passDevice() {
        return false;
    }
	
    // 匿名可用否
    default boolean allowGuestUser() {
        return logon();
    }
    
    @Deprecated
    default boolean logon() {
        return false;
    }
	
}
