package cn.cerc.mis.config;

import cn.cerc.core.IHandle;
import cn.cerc.core.Utils;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.mis.core.Application;

public class ApplicationConfig {

    /**
     * 本地主机
     */
    public static final String Local_Host = "http://127.0.0.1";

    public static final String App_Path = "/public/";

    public static String rewrite(String form) {
        return ApplicationConfig.App_Path + form;
    }

    /**
     * 服务器角色
     */
    public static final String App_Role_Key = "app.role";
    public static final String App_Role_Master = "master";
    public static final String App_Role_Replica = "replica";

    /**
     * 远程服务地址
     */
    public static final String Rempte_Host_Key = "remote.host";

    public static String getToken(IHandle handle) {
        return (String) handle.getProperty(Application.token);
    }

    public static boolean isMaster() {
        String appRole = ServerConfig.getInstance().getProperty(ApplicationConfig.App_Role_Key, ApplicationConfig.App_Role_Master);
        return ApplicationConfig.App_Role_Master.equals(appRole);
    }

    public static boolean isReplica() {
        return !ApplicationConfig.isMaster();
    }

    /**
     * 生成token字符串
     */
    public static final String generateToken() {
        String guid = Utils.newGuid();
        String str = guid.substring(1, guid.length() - 1);
        return str.replaceAll("-", "");
    }

}
