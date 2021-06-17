package cn.cerc.mis.config;

import cn.cerc.core.LanguageResource;

public class ApplicationConfig {

    @Deprecated
    public static final String App_Path = "/public/";

    @Deprecated
    public static String rewrite(String form) {
        return ApplicationConfig.App_Path + form;
    }

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

}
