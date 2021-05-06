package cn.cerc.mis.config;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.LanguageResource;
import cn.cerc.mis.SummerMIS;

public class ApplicationConfig {
    private static final ClassConfig config = new ClassConfig(ApplicationConfig.class, SummerMIS.ID);

    @Deprecated
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

    // TODO: 2021/3/11 后需要改为使用配置文件的方式
    public static final String PATTERN_CN = "0.##";
    public static final String PATTERN_TW = ",###.####";
    public static final String NEGATIVE_PATTERN_TW = "#,###0;(#,###0)";

    public static String getPattern() {
        if (LanguageResource.isLanguageTW()) {
            return ApplicationConfig.PATTERN_TW;
        } else {
            return ApplicationConfig.PATTERN_CN;
        }
    }

    @Deprecated
    public static boolean isMaster() {
        String appRole = config.getString(ApplicationConfig.App_Role_Key, ApplicationConfig.App_Role_Master);
        return ApplicationConfig.App_Role_Master.equals(appRole);
    }

    @Deprecated
    public static boolean isReplica() {
        return !ApplicationConfig.isMaster();
    }

}
