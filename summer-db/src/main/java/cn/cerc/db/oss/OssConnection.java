package cn.cerc.db.oss;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;

import cn.cerc.core.IConfig;
import cn.cerc.core.IConnection;
import cn.cerc.db.core.ServerConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OssConnection implements IConnection {

    // 设置连接地址
    public static final String oss_endpoint = "oss.endpoint";
    // 连接区域
    public static final String oss_bucket = "oss.bucket";
    // 对外访问地址
    public static final String oss_site = "oss.site";
    // 连接id
    public static final String oss_accessKeyId = "oss.accessKeyId";
    // 连接密码
    public static final String oss_accessKeySecret = "oss.accessKeySecret";
    // IHandle 标识
    public static final String sessionId = "ossSession";
    private static OSSClient client;
    private static String bucket;
    private static String site;
    private IConfig config;

    public OssConnection() {
        config = ServerConfig.getInstance();
    }

    @Override
    public OSSClient getClient() {
        if (client != null) {
            return client;
        }

        // 如果连接被意外断开了,那么重新建立连接
        if (null == client) {
            String endPoint = config.getProperty(OssConnection.oss_endpoint, null);
            String acId = config.getProperty(OssConnection.oss_accessKeyId, null);
            String secret = config.getProperty(OssConnection.oss_accessKeySecret, null);
            bucket = config.getProperty(OssConnection.oss_bucket, null);
            // 创建ClientConfiguration实例
            ClientConfiguration conf = new ClientConfiguration();
            // 设置OSSClient使用的最大连接数，默认1024
            conf.setMaxConnections(1024);
            // 设置请求超时时间，默认3秒
            conf.setSocketTimeout(3 * 1000);
            // 设置失败请求重试次数，默认3次
            conf.setMaxErrorRetry(3);

            site = config.getProperty(OssConnection.oss_site);

            // 创建OSSClient实例
            client = new OSSClient(endPoint, acId, secret, conf);
            log.debug("建立oss连接成功");
        }

        return client;
    }

    // 获取指定的数据库是否存在
    public boolean exist(String bucket) {
        return getClient().doesBucketExist(bucket);
    }

    // 获取所有的列表
    public List<Bucket> getBuckets() {
        return getClient().listBuckets();
    }

    public String getBucket() {
        return bucket;
    }

    // 上传文件
    public void upload(String fileName, InputStream inputStream) {
        // 例：upload(inputStream, "131001/Default/131001/temp.txt")
        getClient().putObject(getBucket(), fileName, inputStream);
    }

    // 下载文件
    public boolean download(String fileName, String localFile) {
        GetObjectRequest param = new GetObjectRequest(getBucket(), fileName);
        File file = new File(localFile);
        ObjectMetadata metadata = getClient().getObject(param, file);
        return file.exists() && metadata.getContentLength() == file.length();
    }

    // 删除文件
    public void delete(String fileName) {
        getClient().deleteObject(getBucket(), fileName);
    }

    public String getContent(String fileName) {
        try {
            StringBuffer sb = new StringBuffer();
            // ObjectMetadata meta = client.getObjectMetadata(this.getBucket(),
            // fileName);
            // if (meta.getContentLength() == 0)
            // return null;
            OSSObject obj = getClient().getObject(getBucket(), fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(obj.getObjectContent()));
            while (true) {
                String line;
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line);
            }
            return sb.toString();
        } catch (OSSException | IOException e) {
            return null;
        }
    }

    /**
     * 判断指定的文件名是否存在
     *
     * @param fileName 带完整路径的文件名
     * @return 若存在则返回true
     */
    public boolean existsFile(String fileName) {
        try {
            OSSObject obj = getClient().getObject(getBucket(), fileName);
            return obj.getObjectMetadata().getContentLength() > 0;
        } catch (OSSException e) {
            return false;
        }
    }

    /**
     * 返回可用的文件名称
     *
     * @param fileName 带完整路径的文件名
     * @param def      默认值
     * @return 若存在则返回路径，否则返回默认值
     */
    public String getFileUrl(String fileName, String def) {
        if (existsFile(fileName)) {
            return String.format("%s/%s", site, fileName);
        } else {
            return def;
        }
    }

    public String getSite() {
        return site;
    }

    @Override
    public String getClientId() {
        return sessionId;
    }

    public IConfig getConfig() {
        return config;
    }

    @Override
    public void setConfig(IConfig config) {
        this.config = config;
    }

}
