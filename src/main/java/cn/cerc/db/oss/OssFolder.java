package cn.cerc.db.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OssFolder {
    private OssDisk disk;
    private String name;
    private List<String> subItems = new ArrayList<>();
    private Map<String, OSSObjectSummary> files = new HashMap<>();

    public OssFolder(OssDisk disk) {
        this.disk = disk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSubItems() {
        return subItems;
    }

    public Map<String, OSSObjectSummary> getFiles() {
        return files;
    }

    public void open(String folderName) {
        this.setName(folderName);
        this.files.clear();
        this.subItems.clear();
        OSS client = disk.getClient();

        String marker = "";
        while (true) {
            // 构造ListObjectsRequest请求
            ListObjectsRequest params = new ListObjectsRequest(disk.getConnection().getBucket());

            // 设置参数
            params.setDelimiter("/");
            params.setMarker(marker);
            // params.setMaxKeys(1000);
            if (this.name != null && !"".equals(this.name) && !"/".equals(this.name)) {
                // log.info("Prefix: " + this.name);
                params.setPrefix(this.name);
            }

            // List Objects
            ObjectListing listing = client.listObjects(params);

            // 遍历所有Object
            // log.info("Objects:");
            for (OSSObjectSummary item : listing.getObjectSummaries()) {
                if (!item.getKey().equals(params.getPrefix())) {
                    files.put(item.getKey(), item);
                }
                // log.info(item.getKey());
            }

            // 遍历所有CommonPrefix
            // log.info("CommonPrefixs:");
            for (String item : listing.getCommonPrefixes()) {
                subItems.add(item);
                // log.info(item);
                marker = item;
            }

            // 判断是否要继续找
            if (listing.getCommonPrefixes().size() == 0) {
                // log.info("没有找到: " + this.name);
                break;
            }
        }
    }

}
