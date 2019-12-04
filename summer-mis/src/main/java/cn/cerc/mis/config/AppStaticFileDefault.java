package cn.cerc.mis.config;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AppStaticFileDefault implements IAppStaticFile {

    private static List<String> suffix = new ArrayList<>();

    {
        // 网页文件
        suffix.add(".css");
        suffix.add(".js");
        suffix.add(".jsp");
        suffix.add(".htm");
        suffix.add(".html");

        // 图片文件
        suffix.add(".jpg");
        suffix.add(".png");
        suffix.add(".gif");
        suffix.add(".icon");
        suffix.add(".bmp");

        // 音频文件
        suffix.add(".mp3");

        // 视频文件
        suffix.add(".mp4");

        // 安装文件
        suffix.add(".apk");
        suffix.add(".exe");
        suffix.add(".manifest");
    }

    @Override
    public boolean isStaticFile(String uri) {
        return suffix.stream().anyMatch(uri::endsWith);
    }

}
