package cn.cerc.ui.core;

import cn.cerc.core.Utils;
import cn.cerc.db.core.ServerConfig;

public final class HtmlWriter {
    private final StringBuilder builder = new StringBuilder();

    public final HtmlWriter print(String value) {
        builder.append(value);
        return this;
    }

    public final HtmlWriter print(String format, Object... args) {
        builder.append(String.format(format, args));
        return this;
    }

    public final HtmlWriter println(String value) {
        builder.append(value);
        if (ServerConfig.isServerDevelop()) {
            builder.append(Utils.vbCrLf);
        }
        return this;
    }

    public final HtmlWriter println(String format, Object... args) {
        builder.append(String.format(format, args));
        if (ServerConfig.isServerDevelop()) {
            builder.append(Utils.vbCrLf);
        }
        return this;
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
