package cn.cerc.mis.client;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import cn.cerc.db.core.LocalConfig;
import cn.cerc.mis.core.RequestData;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

@Slf4j
public class RemoteService implements IServiceProxy {

    private String host = "127.0.0.1";
    private String path;
    private String service;
    private String token;

    private DataSet dataIn;
    private DataSet dataOut;
    private String message;

    public RemoteService() {
        LocalConfig localConfig = LocalConfig.getInstance();
        this.host = localConfig.getProperty("remote.host", "127.0.0.1");
    }

    public RemoteService(String bookNo, String service) {
        LocalConfig localConfig = LocalConfig.getInstance();
        this.host = localConfig.getProperty("remote.host", "127.0.0.1");
        this.path = bookNo;
        this.service = service;
    }

    @Override
    public String getService() {
        return service;
    }

    @Override
    public IServiceProxy setService(String service) {
        this.service = service;
        return this;
    }

    @Override
    public boolean exec(Object... args) {
        if (args.length > 0) {
            Record headIn = getDataIn().getHead();
            if (args.length % 2 != 0)
                throw new RuntimeException("传入的参数数量必须为偶数！");
            for (int i = 0; i < args.length; i = i + 2)
                headIn.setField(args[i].toString(), args[i + 1]);
        }

        String postParam = getDataIn().getJSON();
        String url = this.getUrl();
        try {
            log.info("dataIn {}", postParam);
             String response = postData(url, postParam);
            log.info("response {}", response);

            if (response == null) {
                return false;
            }

            JSONObject json = JSONObject.fromObject(response);
            if (json.get("message") != null) {
                this.setMessage(json.getString("message"));
            }

            if (json.containsKey("dataOut")) {
                String dataJson = json.getString("dataOut");
                if (dataJson != null) {
                    this.getDataOut().setJSON(dataJson);
                }
            }
            return json.getBoolean("result");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e.getCause() != null) {
                setMessage(e.getCause().getMessage());
            } else {
                setMessage(e.getMessage());
            }
            return false;
        }
    }

    private String postData(String url, String params) throws ClientProtocolException, IOException {
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity = new StringEntity(params.toString(), "UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);

        log.debug("post: " + url);
        HttpClient client = HttpClientBuilder.create().build();

        HttpResponse response = client.execute(httpPost);
        // 如果请求成功
        if (response.getStatusLine().getStatusCode() != 200) {
            this.setMessage("请求服务器失败，错误代码为：" + response.getStatusLine().getStatusCode());
            return null;
        }

        // 获取响应实体
        HttpEntity entity2 = response.getEntity();
        return EntityUtils.toString(entity2, "UTF-8");
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataSet getDataOut() {
        if (dataOut == null)
            dataOut = new DataSet();
        return dataOut;
    }

    protected void setDataOut(DataSet dataOut) {
        this.dataOut = dataOut;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public DataSet getDataIn() {
        if (dataIn == null)
            dataIn = new DataSet();
        return dataIn;
    }

    public void setDataIn(DataSet dataIn) {
        this.dataIn = dataIn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        // host + path + serviceName
        if (this.token == null || "".equals(this.token)) {
            return String.format("%s/%s/ProxyService?service=%s", this.host, this.path, this.service);
        } else {
            return String.format("%s/%s/ProxyService?service=%s?%s=%s", this.host, this.path, this.service, RequestData.TOKEN, this.token);
        }
    }

}
