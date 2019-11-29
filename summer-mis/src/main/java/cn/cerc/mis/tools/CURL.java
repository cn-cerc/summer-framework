package cn.cerc.mis.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * HTTP请求代理类
 * </pre>
 *
 * @author benl
 * @version 1.0, 2007-7-3
 */
/** 请改使用summer-db的Curl对象 */
@Deprecated
public class CURL {

    /**
     * 连接超时
     */
    private static int connectTimeOut = 5000;

    /**
     * 读取数据超时
     */
    private static int readTimeOut = 10000;

    /**
     * 请求编码
     */
    private static String requestEncoding = "UTF-8";

    private static Logger log = LoggerFactory.getLogger(CURL.class);

    // 发送带参数的GET的HTTP请求, reqUrl HTTP请求URL param parameters 参数映射表 return
    // HTTP响应的字符串
    public static String doGet(String reqUrl) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        return CURL.doGet(reqUrl, parameters, "UTF-8");
    }

    public static String doGet(String reqUrl, Map<String, Object> parameters, String recvEncoding) {
        HttpURLConnection url_con = null;
        String responseContent = null;
        try {
            StringBuffer params = new StringBuffer();
            for (Iterator<?> iter = parameters.entrySet().iterator(); iter.hasNext();) {
                Entry<?, ?> element = (Entry<?, ?>) iter.next();
                params.append(element.getKey().toString());
                params.append("=");
                params.append(URLEncoder.encode(element.getValue().toString(), CURL.requestEncoding));
                params.append("&");
            }

            if (params.length() > 0) {
                params = params.deleteCharAt(params.length() - 1);
            }

            URL url = new URL(reqUrl);
            url_con = (HttpURLConnection) url.openConnection();
            url_con.setRequestMethod("GET");
            System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(CURL.connectTimeOut));// （单位：毫秒）jdk1.4换成这个,连接超时
            System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(CURL.readTimeOut)); // （单位：毫秒）jdk1.4换成这个,读操作超时
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
            StringBuffer temp = new StringBuffer();
            String crlf = System.getProperty("line.separator");
            while (tempLine != null) {
                temp.append(tempLine);
                temp.append(crlf);
                tempLine = rd.readLine();
            }
            responseContent = temp.toString();
            rd.close();
            in.close();
        } catch (IOException e) {
            log.error("网络故障", e);
        } finally {
            if (url_con != null) {
                url_con.disconnect();
            }
        }

        return responseContent;
    }

    // 发送不带参数的GET的HTTP请求, reqUrl HTTP请求URL return HTTP响应的字符串
    public static String doGet(String reqUrl, String recvEncoding) {
        HttpURLConnection url_con = null;
        String responseContent = null;
        try {
            StringBuffer params = new StringBuffer();
            String queryUrl = reqUrl;
            int paramIndex = reqUrl.indexOf("?");

            if (paramIndex > 0) {
                queryUrl = reqUrl.substring(0, paramIndex);
                String parameters = reqUrl.substring(paramIndex + 1, reqUrl.length());
                String[] paramArray = parameters.split("&");
                for (int i = 0; i < paramArray.length; i++) {
                    String string = paramArray[i];
                    int index = string.indexOf("=");
                    if (index > 0) {
                        String parameter = string.substring(0, index);
                        String value = string.substring(index + 1, string.length());
                        params.append(parameter);
                        params.append("=");
                        params.append(URLEncoder.encode(value, CURL.requestEncoding));
                        params.append("&");
                    }
                }

                params = params.deleteCharAt(params.length() - 1);
            }

            URL url = new URL(queryUrl);
            url_con = (HttpURLConnection) url.openConnection();
            url_con.setRequestMethod("GET");
            System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(CURL.connectTimeOut));// （单位：毫秒）jdk1.4换成这个,连接超时
            System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(CURL.readTimeOut)); // （单位：毫秒）jdk1.4换成这个,读操作超时
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
            StringBuffer temp = new StringBuffer();
            String crlf = System.getProperty("line.separator");
            while (tempLine != null) {
                temp.append(tempLine);
                temp.append(crlf);
                tempLine = rd.readLine();
            }
            responseContent = temp.toString();
            rd.close();
            in.close();
        } catch (IOException e) {
            log.error("网络故障", e);
        } finally {
            if (url_con != null) {
                url_con.disconnect();
            }
        }

        return responseContent;
    }

    // 发送带参数的POST的HTTP请求, reqUrl HTTP请求URL param parameters 参数映射表 return
    // HTTP响应的字符串
    public static String doPost(String reqUrl, Map<String, String> parameters, String recvEncoding) {
        try {
            StringBuffer params = new StringBuffer();
            for (Iterator<?> iter = parameters.entrySet().iterator(); iter.hasNext();) {
                Entry<?, ?> element = (Entry<?, ?>) iter.next();
                Object val = element.getValue();
                if (val != null) {
                    params.append(element.getKey().toString());
                    params.append("=");
                    params.append(URLEncoder.encode(val.toString(), CURL.requestEncoding));
                    params.append("&");
                }
            }

            if (params.length() > 0) {
                params = params.deleteCharAt(params.length() - 1);
            }

            return doPost(reqUrl, params, recvEncoding);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }

    }

    public static String doPost(String reqUrl, StringBuffer params, String recvEncoding) {
        HttpURLConnection url_con = null;
        String responseContent = null;
        try {
            URL url = new URL(reqUrl);
            url_con = (HttpURLConnection) url.openConnection();
            url_con.setRequestMethod("POST");
            // System.setProperty("sun.net.client.defaultConnectTimeout",
            // String.valueOf(CURL.connectTimeOut));// （单位：毫秒）jdk1.4换成这个,连接超时
            // System.setProperty("sun.net.client.defaultReadTimeout",
            // String.valueOf(CURL.readTimeOut)); // （单位：毫秒）jdk1.4换成这个,读操作超时
            url_con.setConnectTimeout(CURL.connectTimeOut);// （单位：毫秒）jdk
            // 1.5换成这个,连接超时
            url_con.setReadTimeout(CURL.readTimeOut);// （单位：毫秒）jdk 1.5换成这个,读操作超时
            url_con.setDoOutput(true);

            byte[] b = params.toString().getBytes();
            url_con.getOutputStream().write(b, 0, b.length);
            url_con.getOutputStream().flush();
            url_con.getOutputStream().close();

            InputStream in = url_con.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in, recvEncoding));
            String tempLine = rd.readLine();
            StringBuffer tempStr = new StringBuffer();
            String crlf = System.getProperty("line.separator");
            while (tempLine != null) {
                tempStr.append(tempLine);
                tempStr.append(crlf);
                tempLine = rd.readLine();
            }
            responseContent = tempStr.toString();
            rd.close();
            in.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        } finally {
            if (url_con != null) {
                url_con.disconnect();
            }
        }
        return responseContent;
    }

    // return 连接超时(毫秒)
    public static int getConnectTimeOut() {
        return CURL.connectTimeOut;
    }

    // 读取数据超时(毫秒)
    public static int getReadTimeOut() {
        return CURL.readTimeOut;
    }

    // 请求编码
    public static String getRequestEncoding() {
        return requestEncoding;
    }

    // 连接超时(毫秒)
    public static void setConnectTimeOut(int connectTimeOut) {
        CURL.connectTimeOut = connectTimeOut;
    }

    // 读取数据超时(毫秒)
    public static void setReadTimeOut(int readTimeOut) {
        CURL.readTimeOut = readTimeOut;
    }

    // 请求编码
    public static void setRequestEncoding(String requestEncoding) {
        CURL.requestEncoding = requestEncoding;
    }
}
