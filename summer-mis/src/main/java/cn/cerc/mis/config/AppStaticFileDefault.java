package cn.cerc.mis.config;

import java.util.ArrayList;
import java.util.List;

public enum AppStaticFileDefault {

    INSTANCE;

    public static AppStaticFileDefault getInstance() {
        return INSTANCE;
    }

    private static final List<String> suffix = new ArrayList<>();

    static {
        // 网页文件
        suffix.add(".css");
        suffix.add(".js");
        suffix.add(".jsp");
        suffix.add(".htm");
        suffix.add(".html");
        suffix.add(".map");

        // 图片文件
        suffix.add(".jpg");
        suffix.add(".png");
        suffix.add(".gif");
        suffix.add(".icon");
        suffix.add(".bmp");
        suffix.add(".ico");

        // 音频文件
        suffix.add(".mp3");

        // 视频文件
        suffix.add(".mp4");

        // 安装文件
        suffix.add(".apk");
        suffix.add(".exe");
        suffix.add(".manifest");
        suffix.add(".ttf");
        suffix.add(".woff");
        suffix.add(".woff2");
    }

    public boolean isStaticFile(String uri) {
        return suffix.stream().anyMatch(uri::endsWith);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 5555; i++) {
            new Thread(() -> {
                AppStaticFileDefault.getInstance().isStaticFile(".ico");
            }).start();
        }
    }

}
