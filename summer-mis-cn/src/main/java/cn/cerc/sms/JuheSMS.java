package cn.cerc.sms;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JuheSMS {
    private static final Logger log = LoggerFactory.getLogger(JuheSMS.class);

    public static final String DEF_CHATSET = "UTF-8";
    public static final int DEF_CONN_TIMEOUT = 5000;
    public static final int DEF_READ_TIMEOUT = 5000;
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";
    /**
     * 请求地址
     **/
    private static final String Juhe_Url = "http://v.juhe.cn/sms/send";
    private static Map<String, String> items = new HashMap<>();

    static {
        // 系统级错误码参照
        items.put("10001", "错误的请求KEY");
        items.put("10002", "该KEY无请求权限");
        items.put("10003", "KEY过期");
        items.put("10004", "错误的OPENID");
        items.put("10005", "应用未审核超时，请提交认证");
        items.put("10007", "未知的请求源");
        items.put("10008", "被禁止的IP");
        items.put("10009", "被禁止的KEY");
        items.put("10011", "当前IP请求超过限制");
        items.put("10012", "请求超过次数限制");
        items.put("10013", "测试KEY超过请求限制");
        items.put("10014", "系统内部异常");
        items.put("10020", "接口维护");
        items.put("10021", "接口停用");

        // 服务级错误码参照
        items.put("205401", "错误的手机号码");
        items.put("205402", "错误的短信模板ID");
        items.put("205403", "网络错误,请重试");
        items.put("205404", "发送失败，具体原因请参考返回reason");
        items.put("205405", "号码异常/同一号码发送次数过于频繁");
        items.put("205406", "不被支持的模板");
    }

    private String message;
    private String apiKey;

    public JuheSMS(String appKey) {
        this.apiKey = appKey;
    }

    /**
     * @param mobile     接收者手机号
     * @param templateId 短信模板编号
     * @param text       内容格式化
     * @return 发送结果
     */
    public boolean send(String mobile, String templateId, String text) {
        try {
            Map<String, Object> params = new HashMap<>();
            // 接收短信的手机号码
            params.put("mobile", mobile);
            // 短信模板ID，请参考个人中心短信模板设置
            params.put("tpl_id", templateId);
            // 变量名和变量值对
            params.put("tpl_value", URLEncoder.encode(text, "UTF-8"));
            // 应用APPKEY(应用详细页查询)
            params.put("key", this.apiKey);
            // 返回数据的格式,xml或json，默认json
            params.put("dtype", "");

            String response = post(Juhe_Url, params, "GET");

            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response);

            if (json.get("error_code").asInt() == 0) {
                String tailNumber = mobile.substring(mobile.length() - 4);
                this.setMessage(String.format(RS.验证码发送成功, tailNumber));

                String result = json.get("result").asText();
                log.info("send: {}, templateId: {}, result: {}", mobile, templateId, result);
                return true;
            } else {
                String error_code = json.get("error_code").asText();
                this.setMessage("%s: %s", error_code, items.get(error_code));
                log.error("send: {}, templateId: {}, result: {}", mobile, templateId, getMessage());
                return false;
            }
        } catch (Exception e) {
            this.setMessage(e.getMessage());
            log.error("send: {}, templateId: {}, result: {}", mobile, templateId, getMessage());
            return false;
        }
    }

    protected String post(String strUrl, Map<String, Object> params, String method) throws Exception {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String rs = null;
        try {
            StringBuffer sb = new StringBuffer();
            if (method == null || "GET".equals(method)) {
                strUrl = strUrl + "?" + urlEncode(params);
            }
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            if (method == null || "GET".equals(method)) {
                conn.setRequestMethod("GET");
            } else {
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
            }
            conn.setRequestProperty("User-agent", USER_AGENT);
            conn.setUseCaches(false);
            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
            conn.setReadTimeout(DEF_READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);
            conn.connect();
            if (params != null && "POST".equals(method)) {
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
    private String urlEncode(Map<String, Object> data) {
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

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessage(String format, Object... args) {
        this.message = String.format(format, args);
    }

}