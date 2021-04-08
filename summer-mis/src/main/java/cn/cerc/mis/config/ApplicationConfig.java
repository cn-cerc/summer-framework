package cn.cerc.mis.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.ClassResource;
import cn.cerc.core.LanguageResource;
import cn.cerc.core.Record;
import cn.cerc.core.Utils;
import cn.cerc.db.core.IHandle;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.core.AppClient;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.CenterService;

public class ApplicationConfig {
    private static final Logger log = LoggerFactory.getLogger(ApplicationConfig.class);
    private static final ClassResource res = new ClassResource(ApplicationConfig.class, SummerMIS.ID);
    private static final ClassConfig config = new ClassConfig(ApplicationConfig.class, SummerMIS.ID);

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

    /**
     * 远程服务地址
     */
    @Deprecated
    public static final String Rempte_Host_Key = "remote.host";

    /**
     * 请改使用Application.getToken函数
     * 
     * @param handle
     * @return
     */
    @Deprecated
    public static String getToken(IHandle handle) {
        return handle.getSession().getToken();
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

    /**
     * 向public服务器获取授权令牌
     *
     * @param userCode    用户代码
     * @param password    用户密码
     * @param machineCode 设备代码
     * @return 用户授权令牌 token
     */
    public static String getAuthToken(String userCode, String password, String machineCode, IHandle handle) {
        if (Utils.isEmpty(userCode)) {
            throw new RuntimeException(String.format(res.getString(1, "%s 不允许为空"), "userCode"));
        }
        if (Utils.isEmpty(password)) {
            throw new RuntimeException(String.format(res.getString(1, "%s 不允许为空"), "password"));
        }
        if (Utils.isEmpty(machineCode)) {
            throw new RuntimeException(String.format(res.getString(1, "%s 不允许为空"), "machineCode"));
        }

        CenterService svr = new CenterService(handle);
        svr.setService("SvrLogin.getToken");
        Record headIn = svr.getDataIn().getHead();
        headIn.setField("userCode", userCode);
        headIn.setField("password", password);
        headIn.setField("clientId", machineCode);
        headIn.setField("device", AppClient.pc);
        headIn.setField("languageId", Application.App_Language);
        headIn.setField("access", AccessLevel.Access_Task);// 访问层级获取队列授权
        if (!svr.exec()) {
            throw new RuntimeException(svr.getMessage());
        }
        String token = svr.getDataOut().getHead().getString("token");
        log.debug("userCode {} token {}", userCode, token);
        if (Utils.isEmpty(token)) {
            throw new RuntimeException(res.getString(3, "服务器没有返回token"));
        }
        return token;
    }

}
