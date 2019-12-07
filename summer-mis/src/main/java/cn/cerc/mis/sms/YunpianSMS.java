package cn.cerc.mis.sms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.IHandle;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.language.R;
import net.sf.json.JSONObject;

public class YunpianSMS {
    private static final Logger log = LoggerFactory.getLogger(YunpianSMS.class);
    private String mobile;
    private String apiurl;
    private String apikey;
    private String message;

    public YunpianSMS(String mobile) {
        this.mobile = mobile;
        // 读取第三方简讯发送配置
        ServerConfig config = ServerConfig.getInstance();
        apiurl = config.getProperty("yun.serverUrl");
        apikey = config.getProperty("yun.apikey");
    }

    public boolean sendText(String text) {
        // 生成调用参数
        Map<String, String> params = new HashMap<String, String>();
        params.put("apikey", apikey);
        params.put("text", text);
        params.put("mobile", mobile);
        // 编码格式。发送编码格式统一用UTF-8
        String ENCODING = "UTF-8";
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            HttpPost method = new HttpPost(apiurl);
            if (params != null) {
                List<NameValuePair> paramList = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> param : params.entrySet()) {
                    NameValuePair pair = new BasicNameValuePair(param.getKey(), param.getValue());
                    paramList.add(pair);
                }
                method.setEntity(new UrlEncodedFormEntity(paramList, ENCODING));
            }
            response = client.execute(method);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String msg = EntityUtils.toString(entity, ENCODING);
                log.debug("msg: " + msg);
                JSONObject json = JSONObject.fromObject(msg);
                if (json.has("code") && json.getInt("code") == 0) {
                    log.info("sendSMS: " + json.getString("msg") + ", " + mobile + "," + text);
                    return true;
                } else {
                    this.message = json.getString("msg");
                    return false;
                }
            } else {
                IHandle handle = Application.getHandle();
                try {
                    this.message = R.asString(handle, "网络故障，简讯发送请求失败！");
                } finally {
                    handle.close();
                }
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.message = e.getMessage();
            return false;
        } finally {
            try {
                response.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getMessage() {
        return message;
    }
}
