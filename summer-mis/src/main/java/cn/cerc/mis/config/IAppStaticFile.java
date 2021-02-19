package cn.cerc.mis.config;

public interface IAppStaticFile {
    // 判断请求的是否为静态文件
    boolean isStaticFile(String uri);
}
