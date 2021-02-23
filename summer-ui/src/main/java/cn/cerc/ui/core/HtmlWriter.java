package cn.cerc.ui.core;

import cn.cerc.core.Utils;
import cn.cerc.db.core.ServerConfig;

public class HtmlWriter {
    private StringBuilder builder = new StringBuilder();

    public void print(String value) {
        builder.append(value);
    }

    public void print(String format, Object... args) {
        builder.append(String.format(format, args));
    }

    public void println(String value) {
        builder.append(value);
        if (ServerConfig.isServerDevelop()) {
            builder.append(Utils.vbCrLf);
        }
    }

    public void println(String format, Object... args) {
        builder.append(String.format(format, args));
        if (ServerConfig.isServerDevelop()) {
            builder.append(Utils.vbCrLf);
        }
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
