package cn.cerc.mis.config;

import org.springframework.stereotype.Component;

@Component
public class AppStaticFileDefault implements IAppStaticFile {

    @Override
    public boolean isStaticFile(String uri) {
        if (uri.endsWith(".css") || uri.endsWith(".jpg") || uri.endsWith(".gif") || uri.endsWith(".png")
                || uri.endsWith(".bmp") || uri.endsWith(".js") || uri.endsWith(".mp3") || uri.endsWith(".icon")
                || uri.endsWith(".apk") || uri.endsWith(".exe") || uri.endsWith(".jsp") || uri.endsWith(".htm")
                || uri.endsWith(".html") || uri.endsWith(".manifest") || uri.endsWith(".ttf")) {
            return true;
        } else {
            return false;
        }
    }

}
