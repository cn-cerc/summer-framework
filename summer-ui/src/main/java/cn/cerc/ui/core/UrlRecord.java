package cn.cerc.ui.core;

import cn.cerc.core.Utils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class UrlRecord {
    private String site;
    private String name;
    private String title;
    private String target;
    private String hintMsg;
    private String id;
    private String imgage;
    private String arrow;

    private boolean isWindow;

    private Map<String, String> params = new LinkedHashMap<>();

    public static class Builder {
        // Required parameters
        private String site;

        // Optional parameters - initialized to default values
        private String name;
        private String title;
        private String target;
        private String hintMsg;
        private String id;
        private String imgage;
        private String arrow;
        private Map<String, String> params = new HashMap<>();

        public Builder(String site) {
            this.site = site;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder target(String target) {
            this.target = target;
            return this;
        }

        public Builder hintMsg(String hintMsg) {
            this.hintMsg = hintMsg;
            return this;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder imgage(String imgage) {
            this.imgage = imgage;
            return this;
        }

        public Builder put(String key, String value) {
            this.params.put(key, value);
            return this;
        }

        public UrlRecord build() {
            return new UrlRecord(this);
        }

    }

    private UrlRecord(Builder builder) {
        this.site = builder.site;
        this.name = builder.name;
        this.title = builder.title;
        this.target = builder.target;
        this.hintMsg = builder.hintMsg;
        this.id = builder.id;
        this.imgage = builder.imgage;
        this.arrow = builder.arrow;
        this.params = builder.params;
    }

    public UrlRecord() {
    }

    public UrlRecord(String site, String name) {
        super();
        this.site = site;
        this.name = name;
    }

    @Deprecated
    public UrlRecord addParam(String key, String value) {
        this.putParam(key, value);
        return this;
    }

    public UrlRecord putParam(String key, String value) {
        params.put(key, value);
        return this;
    }

    public String getSite() {
        return site;
    }

    public UrlRecord setSite(String site) {
        this.site = site;
        return this;
    }

    public UrlRecord setSite(String format, Object... args) {
        this.site = String.format(format, args);
        return this;
    }

    public String getName() {
        return name;
    }

    public UrlRecord setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * @return 请改为getName
     */
    @Deprecated
    public String getCaption() {
        return name;
    }

    public String getUrl() {
        StringBuilder sl = new StringBuilder();
        if (site != null) {
            sl.append(site);
        }

        int i = 0;
        for (String key : params.keySet()) {
            i++;
            sl.append(i == 1 ? "?" : "&");
            sl.append(key);
            sl.append("=");
            String value = params.get(key);
            if (value != null) {
                sl.append(Utils.encode(value, StandardCharsets.UTF_8.name()));
            }
        }
        return sl.toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTarget() {
        return target;
    }

    public UrlRecord setTarget(String target) {
        this.target = target;
        return this;
    }

    public String getHintMsg() {
        return hintMsg;
    }

    public void setHintMsg(String hintMsg) {
        this.hintMsg = hintMsg;
    }

    public String getId() {
        return id;
    }

    public UrlRecord setId(String id) {
        this.id = id;
        return this;
    }

    public String getImgage() {
        return imgage;
    }

    public UrlRecord setImgage(String imgage) {
        this.imgage = imgage;
        return this;
    }

    public String getArrow() {
        return arrow;
    }

    public UrlRecord setArrow(String arrow) {
        this.arrow = arrow;
        return this;
    }

    public boolean isWindow() {
        return isWindow;
    }

    public void setWindow(boolean window) {
        isWindow = window;
    }

    public enum Target {
        _blank, _self, _parent, _top;
    }

}
