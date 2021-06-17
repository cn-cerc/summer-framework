package cn.cerc.db.oss;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

public class OssClientTest {

    @Test
    @Ignore
    public void test() throws IOException {
        // endpoint以杭州为例，其它region请按实际情况填写
        String endpoint = "";
        // accessKey请登录https://ak-console.aliyun.com/#/查看
        String accessKeyId = "";
        String accessKeySecret = "";
        // 创建ClientConfiguration实例
        ClientConfiguration conf = new ClientConfiguration();
        // 设置OSSClient使用的最大连接数，默认1024
        conf.setMaxConnections(200);
        // 设置请求超时时间，默认50秒
        conf.setSocketTimeout(10000);
        // 设置失败请求重试次数，默认3次
        conf.setMaxErrorRetry(5);
        // 创建OSSClient实例
        OSSClient client = new OSSClient(endpoint, accessKeyId, accessKeySecret, conf);
        // 使用访问OSS
        String uuid = UUID.randomUUID().toString();
        // 存储一个对象
        String content = "Hello OSS";
        client.putObject("zrk-oss-test", uuid, new ByteArrayInputStream(content.getBytes()));
        // 获得对象
        OSSObject ossObject = client.getObject("zrk-oss-test", uuid);
        BufferedReader reader = new BufferedReader(new InputStreamReader(ossObject.getObjectContent()));
        while (true) {
            String line = reader.readLine();
            if (line == null)
                break;
        }
        // 关闭OSSClient
        client.shutdown();
    }
}
