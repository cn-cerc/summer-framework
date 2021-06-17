package cn.cerc.db.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtil {
    private static final Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

    public static String post(String url, String json) {
        try (CloseableHttpClient http = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            StringEntity entity = new StringEntity(json);
            post.setEntity(entity);
            post.addHeader("Content-Type", "application/json;charset=utf-8");
            post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

            CloseableHttpResponse response = http.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                log.error("post response status {}", statusCode);
                return null;
            }
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String get(String url) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        String body = "";
        body = this.invoke(httpClient, get);

        try {
            httpClient.close();
        } catch (IOException var6) {
            log.error("HttpClientService get error", var6);
        }

        return body;
    }

    public String delete(String url) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpDelete delete = new HttpDelete(url);
        String body = this.invoke(httpClient, delete);
        try {
            httpClient.close();
        } catch (IOException var6) {
            log.error("HttpClientService get error", var6);
        }
        return body;
    }

    public String invoke(CloseableHttpClient httpclient, HttpUriRequest request) throws IOException {
        HttpResponse response = httpclient.execute(request);
        String body = "";
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                body = EntityUtils.toString(httpEntity);
            }
        }
        return body;
    }

}
