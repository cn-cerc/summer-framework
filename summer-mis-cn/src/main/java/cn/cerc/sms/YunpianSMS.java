package cn.cerc.sms;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.Handle;
import cn.cerc.mis.language.R;

/**
 * https://www.yunpian.com
 */
public class YunpianSMS {
    private static final Logger log = LoggerFactory.getLogger(YunpianSMS.class);

    // 智能匹配模板发送接口
    private static final String URI_SEND_SMS = "https://sms.yunpian.com/v2/sms/single_send.json";

    // 发送语音验证码接口
    private static final String URI_SEND_VOICE = "https://voice.yunpian.com/v2/voice/send.json";

    private String apikey;
    private String message;
    private boolean sendVoice = false;

    public YunpianSMS(String apiKey) {
        this.apikey = apiKey;
    }

    public boolean sendVoice(String mobile, String text) {
        this.sendVoice = true;
        return this.send(mobile, text);
    }

    /**
     * 云片短信发送
     *
     * @param mobile 接收者手机号
     * @param text   文本内容
     * @return 短信发送结果
     */
    public boolean send(String mobile, String text) {
        // 生成调用参数
        Map<String, String> params = new HashMap<String, String>();
        params.put("apikey", apikey);
        params.put(sendVoice ? "code" : "text", text);
        params.put("mobile", mobile);

        CloseableHttpClient http = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            HttpPost post = new HttpPost(sendVoice ? URI_SEND_VOICE : URI_SEND_SMS);
            List<NameValuePair> paramList = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> param : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(param.getKey(), param.getValue());
                paramList.add(pair);
            }
            post.setEntity(new UrlEncodedFormEntity(paramList, "utf-8"));

            response = http.execute(post);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                String responseText = EntityUtils.toString(entity, "utf-8");
                log.info("response: {}", responseText);

                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(responseText);
                int code = node.get("code").asInt();
                String msg = node.get("msg").asText();
                if (node.has("code") && code == 0) {
                    log.info("sendSMS: {}, {}, {}", msg, mobile, text);
                    this.setMessage(msg);
                    return true;
                } else if (node.has("count") && node.get("count").asInt() > 0) { // 语言信息
                    log.info("sendSMS: {}, {}", mobile, text);
                    return true;
                } else {
                    this.setMessage(msg);
                    log.error("发送失败：{}", msg);
                    return false;
                }
            } else {
                ISession session = Application.createSession();
                IHandle handle = new Handle(session);
                this.setMessage(R.asString(handle, "网络故障，简讯发送请求失败！"));
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.setMessage(e.getMessage());
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

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessage(String format, Object... args) {
        this.message = String.format(format, args);
    }

}
