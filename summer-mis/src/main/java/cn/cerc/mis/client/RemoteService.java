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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RemoteService implements IServiceProxy {
    private static final Logger log = LoggerFactory.getLogger(RemoteService.class);
    private String host = "127.0.0.1";
    private String service;
    private DataSet dataIn;
    private DataSet dataOut;
    private String message;
    private String token;

    public RemoteService() {
    }

    public RemoteService(String service) {
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
        String url = String.format("http://%s/services/%s", this.host, this.service);
        if (token != null)
            url = url + "?token=" + token;
        try {
            log.debug("datain: " + postParam);
            // String rst = CURL.doPost(url, params, "UTF-8");
            String rst = postData(url, postParam);
            log.debug("datatout:" + rst);
            if (rst == null)
                return false;

            JSONObject json = JSONObject.fromObject(rst);
            if (json.get("message") != null) {
                this.setMessage(json.getString("message"));
            }

            if (json.containsKey("data")) {
                JSONArray datas = json.getJSONArray("data");
                if (datas != null && datas.size() > 0) {
                    if (dataOut == null)
                        dataOut = new DataSet();
                    else
                        dataOut.close();
                    dataOut.setJSON(datas.getString(0));
                }
            }
            return json.getBoolean("result");

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e.getCause() != null)
                setMessage(e.getCause().getMessage());
            else
                setMessage(e.getMessage());
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

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return this.host;
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

    protected void setDataOut(DataSet dataOut) {
        this.dataOut = dataOut;
    }
}
