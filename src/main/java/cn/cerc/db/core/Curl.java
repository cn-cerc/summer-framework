package cn.cerc.db.core;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.Utils;

/**
 * HTTP请求代理类
 *
 * @author ZhangGong
 * @version 1.0, 2018-1-1
 */
//FIXME 需重构调用方式初始化直接创建url  new Curl(url);
public class Curl {

    /**
     * 请求编码
     */
    private String requestEncoding = "UTF-8";
    /**
     * 返回的内容编码
     */
    private String recvEncoding = "UTF-8";
    /**
     * 连接超时, 默认5秒
     */
    private int connectTimeOut = 5000;
    /**
     * 读取数据超时，默认10秒
     */
    private int readTimeOut = 10000;
    /**
     * 调用参数
     */
    private final Map<String, Object> parameters = new HashMap<>();
    /**
     * 返回内容
     */
    private String responseContent = null;
    private static final Logger log = LoggerFactory.getLogger(Curl.class);

    public String sendGet(String reqUrl) {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try {

            StringBuilder builder = new StringBuilder();
            builder.append(reqUrl);

            int i = 0;
            for (String key : parameters.keySet()) {
                i++;
                builder.append(i == 1 ? "?" : "&");
                builder.append(key);
                builder.append("=");
                String value = parameters.get(key).toString();
                if (value != null) {
                    builder.append(encodeUTF8(value));
                }
            }
            URL url = new URL(builder.toString());

            // 打开和URL之间的连接
            URLConnection connection = url.openConnection();

            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            // 建立实际的连接
            connection.connect();

            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result.toString();
    }

    private String encodeUTF8(String value) {
        try {
            return URLEncoder.encode(value, requestEncoding);
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    // 发送带参数的GET的HTTP请求
    public String doGet(String reqUrl) {
        if (Utils.isEmpty(reqUrl)) {
            return null;
        }
        HttpURLConnection url_con = null;
        try {
            StringBuffer params = new StringBuffer();
            for (Entry<String, Object> stringObjectEntry : parameters.entrySet()) {
                params.append(((Entry<?, ?>) stringObjectEntry).getKey().toString());
                params.append("=");
                params.append(URLEncoder.encode(((Entry<?, ?>) stringObjectEntry).getValue().toString(),
                        this.requestEncoding));
                params.append("&");
            }

            if (params.length() > 0) {
                params = params.deleteCharAt(params.length() - 1);
            }

            URL url = new URL(reqUrl);
            url_con = (HttpURLConnection) url.openConnection();
            url_con.setRequestMethod("GET");

            url_con.setDoOutput(true);
            byte[] b = params.toString().getBytes();
            url_con.getOutputStream().write(b, 0, b.length);
            url_con.getOutputStream().flush();
            url_con.getOutputStream().close();

            int status = url_con.getResponseCode();
            BufferedInputStream in;
            if (status >= 400) {
                in = new BufferedInputStream(url_con.getErrorStream());
            } else {
                in = new BufferedInputStream(url_con.getInputStream());
            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(in, this.recvEncoding));
            String tempLine = rd.readLine();
            StringBuilder temp = new StringBuilder();
            while (tempLine != null) {
                temp.append(tempLine);
                tempLine = rd.readLine();
            }
            responseContent = temp.toString();
            rd.close();
            in.close();
        } catch (IOException e) {
            log.error("network error", e);
        } finally {
            if (url_con != null) {
                url_con.disconnect();
            }
        }

        return responseContent;
    }

    // 发送不带参数的GET的HTTP请求, reqUrl HTTP请求URL return HTTP响应的字符串
    protected String doGet2(String reqUrl) {
        if (Utils.isEmpty(reqUrl)) {
            return null;
        }
        HttpURLConnection url_con = null;
        try {
            StringBuffer params = new StringBuffer();
            String queryUrl = reqUrl;
            int paramIndex = reqUrl.indexOf("?");

            if (paramIndex > 0) {
                queryUrl = reqUrl.substring(0, paramIndex);
                String parameters = reqUrl.substring(paramIndex + 1);
                String[] paramArray = parameters.split("&");
                for (String string : paramArray) {
                    int index = string.indexOf("=");
                    if (index > 0) {
                        String parameter = string.substring(0, index);
                        String value = string.substring(index + 1);
                        params.append(parameter);
                        params.append("=");
                        params.append(URLEncoder.encode(value, this.requestEncoding));
                        params.append("&");
                    }
                }

                params = params.deleteCharAt(params.length() - 1);
            }

            URL url = new URL(queryUrl);
            url_con = (HttpURLConnection) url.openConnection();
            url_con.setRequestMethod("GET");
            System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(this.connectTimeOut));// （单位：毫秒）jdk1.4换成这个,连接超时
            System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(this.readTimeOut)); // （单位：毫秒）jdk1.4换成这个,读操作超时
            // url_con.setConnectTimeout(5000);//（单位：毫秒）jdk
            // 1.5换成这个,连接超时
            // url_con.setReadTimeout(5000);//（单位：毫秒）jdk 1.5换成这个,读操作超时
            url_con.setDoOutput(true);
            byte[] b = params.toString().getBytes();
            url_con.getOutputStream().write(b, 0, b.length);
            url_con.getOutputStream().flush();
            url_con.getOutputStream().close();
            InputStream in = url_con.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in, recvEncoding));
            String tempLine = rd.readLine();
            StringBuilder temp = new StringBuilder();
            while (tempLine != null) {
                temp.append(tempLine);
                tempLine = rd.readLine();
            }
            responseContent = temp.toString();
            rd.close();
            in.close();
        } catch (IOException e) {
            log.error("network error", e);
        } finally {
            if (url_con != null) {
                url_con.disconnect();
            }
        }

        return responseContent;
    }

    // 发送带参数的POST的HTTP请求
    public String doPost(String reqUrl) {
        if (Utils.isEmpty(reqUrl)) {
            return null;
        }
        try {
            StringBuffer params = new StringBuffer();
            for (Entry<String, Object> stringObjectEntry : parameters.entrySet()) {
                Object val = ((Entry<?, ?>) stringObjectEntry).getValue();
                if (val != null) {
                    params.append(((Entry<?, ?>) stringObjectEntry).getKey().toString());
                    params.append("=");
                    params.append(URLEncoder.encode(val.toString(), this.requestEncoding));
                    params.append("&");
                }
            }

            if (params.length() > 0) {
                params = params.deleteCharAt(params.length() - 1);
            }

            return doPost(reqUrl, params);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }

    }

    public String doPost(String reqUrl, StringBuffer params) {
        if (Utils.isEmpty(reqUrl)) {
            return null;
        }
        HttpURLConnection url_con = null;
        try {
            reqUrl = new String(reqUrl.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

            URL url = new URL(reqUrl);
            url_con = (HttpURLConnection) url.openConnection();
            url_con.setRequestMethod("POST");
            // System.setProperty("sun.net.client.defaultConnectTimeout",
            // String.valueOf(CURL.connectTimeOut));// （单位：毫秒）jdk1.4换成这个,连接超时
            // System.setProperty("sun.net.client.defaultReadTimeout",
            // String.valueOf(CURL.readTimeOut)); // （单位：毫秒）jdk1.4换成这个,读操作超时
            url_con.setConnectTimeout(this.connectTimeOut);// （单位：毫秒）jdk
            // 1.5换成这个,连接超时
            url_con.setReadTimeout(this.readTimeOut);// （单位：毫秒）jdk 1.5换成这个,读操作超时
            url_con.setDoOutput(true);

            byte[] b = params.toString().getBytes();
            url_con.getOutputStream().write(b, 0, b.length);
            url_con.getOutputStream().flush();
            url_con.getOutputStream().close();

            int status = url_con.getResponseCode();
            BufferedInputStream in;
            if (status >= 400) {
                in = new BufferedInputStream(url_con.getErrorStream());
            } else {
                in = new BufferedInputStream(url_con.getInputStream());
            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(in, recvEncoding));
            String tempLine = rd.readLine();
            StringBuilder tempStr = new StringBuilder();
            while (tempLine != null) {
                tempStr.append(tempLine);
                tempLine = rd.readLine();
            }
            responseContent = tempStr.toString();
            rd.close();
            in.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            if (url_con != null) {
                url_con.disconnect();
            }
        }
        return responseContent;
    }

    public String doPost(String url, String json) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            responseContent = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            response.close();
            httpClient.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
        return responseContent;
    }

    public int getConnectTimeOut() {
        return this.connectTimeOut;
    }

    public void setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public int getReadTimeOut() {
        return this.readTimeOut;
    }

    public void setReadTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    public String getRequestEncoding() {
        return requestEncoding;
    }

    public void setRequestEncoding(String requestEncoding) {
        this.requestEncoding = requestEncoding;
    }

    public String getRecvEncoding() {
        return recvEncoding;
    }

    public void setRecvEncoding(String recvEncoding) {
        this.recvEncoding = recvEncoding;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public String getResponseContent() {
        return responseContent;
    }

    @Deprecated // 请改为 putParameter
    public Curl addParameter(String key, Object value) {
        this.parameters.put(key, value);
        return this;
    }

    public Curl put(String key, Object value) {
        this.parameters.put(key, value);
        return this;
    }

    @Deprecated
    public Curl putParameter(String key, Object value) {
        return this.put(key, value);
    }

}
