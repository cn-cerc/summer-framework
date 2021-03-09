package cn.cerc.mis.config;

import cn.cerc.core.ClassResource;
import cn.cerc.core.DataSet;
import cn.cerc.core.IHandle;
import cn.cerc.core.Utils;
import cn.cerc.db.core.HttpClientUtil;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.mis.client.RemoteService;
import cn.cerc.mis.client.ServiceFactory;
import cn.cerc.mis.core.AppClient;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.language.Language;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ApplicationConfig {
    private static final ClassResource res = new ClassResource("summer-mvc", ApplicationConfig.class);

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
     * 向public服务器获取授权令牌
     *
     * @param userCode    用户代码
     * @param password    用户密码
     * @param machineCode 设备代码
     * @return 用户授权令牌 token
     */
    public static String getAuthToken(String userCode, String password, String machineCode) {
        if (Utils.isEmpty(userCode)) {
            throw new RuntimeException(String.format(res.getString(1, "%s 不允许为空"), "userCode"));
        }
        if (Utils.isEmpty(password)) {
            throw new RuntimeException(String.format(res.getString(1, "%s 不允许为空"), "password"));
        }
        if (Utils.isEmpty(machineCode)) {
            throw new RuntimeException(String.format(res.getString(1, "%s 不允许为空"), "machineCode"));
        }

        // 构建public地址
        String host = RemoteService.getApiHost(ServiceFactory.Public);
        String url = host + ApplicationConfig.App_Path + "Login.getToken";
        log.info("request url {}", url);
        // 构建登录请求参数
        DataSet dataIn = new DataSet();
        dataIn.getHead().setField("userCode", userCode);
        dataIn.getHead().setField("password", password);
        dataIn.getHead().setField("clientId", machineCode);
        dataIn.getHead().setField("device", AppClient.pc);
        dataIn.getHead().setField("languageId", Application.App_Language);
        dataIn.getHead().setField("access", AccessLevel.Access_Task);// 访问层级获取队列授权
        String json = dataIn.getJSON();
        log.info("request params {}", json);

        String token;
        try {
            String content = HttpClientUtil.post(url, json);
            log.info("response content {}", content);
            if (Utils.isEmpty(content)) {
                throw new RuntimeException(res.getString(2, "服务器返回内容为空"));
            }

            // 解析post结果
            ObjectMapper mapper = new ObjectMapper();
            JsonNode object = mapper.readTree(content);
            boolean result = object.get("result").asBoolean();
            String message = object.get("message").asText();
            if (!result) {
                log.error("userCode {} init token failure", userCode);
                throw new RuntimeException(message);
            }

            // 取消外围 []，还原标准的dataSet格式
            String data = object.get("data").toString();
            data = data.substring(1, data.length() - 1);

            DataSet dataSet = new DataSet();
            dataSet.setJSON(data);

            token = dataSet.getHead().getString("token");
            log.info("userCode {} token {}", userCode, token);
            if (Utils.isEmpty(token)) {
                throw new RuntimeException(res.getString(3, "服务器没有返回token"));
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
        return token;
    }

}
