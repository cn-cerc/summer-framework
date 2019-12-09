package cn.cerc.mis.config;

public class AppProperty {

    public static final String App_Path = "/public/";

    public static String rewrite(String form) {
        return AppProperty.App_Path + form;
    }

    public static final String App_Role_Key = "app.role";

    public static final String App_Role_Master = "master";

    public static final String App_Role_Replica = "replica";
}
