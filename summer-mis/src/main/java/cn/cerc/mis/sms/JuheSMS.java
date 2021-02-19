package cn.cerc.mis.sms;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.db.core.ServerConfig;
import net.sf.json.JSONObject;

/**
 * 短信API服务调用示例代码 － 聚合数据 在线接口文档：http://www.juhe.cn/docs/54
 **/

public class JuheSMS {
    private static final Logger log = LoggerFactory.getLogger(JuheSMS.class);
    public static final String DEF_CHATSET = "UTF-8";
    public static final int DEF_CONN_TIMEOUT = 5000;
    public static final int DEF_READ_TIMEOUT = 5000;
    public static final String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";
    //
    private String mobile;
    private String message;

    // 配置您申请的KEY
    public static String APPKEY = "";

    static {
        ServerConfig config = ServerConfig.getInstance();
        APPKEY = config.getProperty("juhe.sms");// juhe sms
    }

    public JuheSMS(String mobile) {
        this.mobile = mobile;
    }

    // 1.屏蔽词检查测
    protected void getRequest1() {
        String result = null;
        String url = "http://v.juhe.cn/sms/black";// 请求接口地址
        Map<String, Object> params = new HashMap<>();// 请求参数
        params.put("word", "");// 需要检测的短信内容，需要UTF8 URLENCODE
        params.put("key", APPKEY);// 应用APPKEY(应用详细页查询)

        try {
            result = post(url, params, "GET");
            JSONObject object = JSONObject.fromObject(result);
            if (object.getInt("error_code") == 0) {
                log.info("" + object.get("result"));
            } else {
                log.info("" + object.get("error_code") + ":" + object.get("reason"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 2.发送短信
    public boolean sendByTemplateId(String templateId, String templateValues) {
        String result = null;
        String url = "http://v.juhe.cn/sms/send";// 请求接口地址
        Map<String, Object> params = new HashMap<>();// 请求参数
        params.put("mobile", mobile);// 接收短信的手机号码
        params.put("tpl_id", templateId);// 短信模板ID，请参考个人中心短信模板设置
        params.put("tpl_value", templateValues);// 变量名和变量值对。如果你的变量名或者变量值中带有#&=中的任意一个特殊符号，请先分别进行urlencode编码后再传递，<a
        // href="http://www.juhe.cn/news/index/id/50"
        // target="_blank">详细说明></a>
        params.put("key", APPKEY);// 应用APPKEY(应用详细页查询)
        params.put("dtype", "");// 返回数据的格式,xml或json，默认json

        try {
            result = post(url, params, "GET");
            JSONObject object = JSONObject.fromObject(result);
            if (object.getInt("error_code") == 0) {
                log.info("" + object.get("result"));
                this.message = object.getString("result");
                log.info(String.format("send: %s, templateId: %s, result: %s", mobile, templateId, message));
                return true;
            } else {
                this.message = object.get("error_code") + ":" + object.get("reason");
                log.warn(String.format("send: %s, templateId: %s, result: %s", mobile, templateId, message));
                return false;
            }
        } catch (Exception e) {
            this.message = e.getMessage();
            log.error(String.format("send: %s, templateId: %s, result: %s", mobile, templateId, message));
            return false;
        }
    }

    protected String post(String strUrl, Map<String, Object> params, String method) throws Exception {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String rs = null;
        try {
            StringBuffer sb = new StringBuffer();
            if (method == null || method.equals("GET")) {
                strUrl = strUrl + "?" + urlEncode(params);
            }
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            if (method == null || method.equals("GET")) {
                conn.setRequestMethod("GET");
            } else {
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
            }
            conn.setRequestProperty("User-agent", userAgent);
            conn.setUseCaches(false);
            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
            conn.setReadTimeout(DEF_READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);
            conn.connect();
            if (params != null && method.equals("POST")) {
                try {
                    DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                    out.writeBytes(urlEncode(params));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sb.append(strRead);
            }
            rs = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rs;
    }

    // 将map型转为请求参数型
    protected String urlEncode(Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();
        try {
            for (String key : data.keySet()) {
                String item = (String) data.get(key);
                sb.append(key).append("=");
                sb.append(URLEncoder.encode(item + "", "UTF-8")).append("&");
            }
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        return sb.toString();
    }

    public String getMessage() {
        return message;
    }
}