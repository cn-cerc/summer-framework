package cn.cerc.mis.config;

import cn.cerc.core.IHandle;
import cn.cerc.mis.core.Application;

public class AppProperty {

    /**
     * 本地主机
     */
    public static final String Local_Host = "http://127.0.0.1";

    public static final String App_Path = "/public/";

    public static String rewrite(String form) {
        return AppProperty.App_Path + form;
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

}
