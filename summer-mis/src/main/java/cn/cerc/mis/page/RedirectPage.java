package cn.cerc.mis.page;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import cn.cerc.mis.core.IForm;
import cn.cerc.mis.core.IPage;

public class RedirectPage implements IPage {
    private String url;
    protected IForm form;
    private Map<String, String> params = new HashMap<>();

    public RedirectPage() {
        super();
    }

    public RedirectPage(IForm form) {
        super();
        this.setForm(form);
    }

    public RedirectPage(IForm form, String url) {
        super();
        this.setForm(form);
        this.url = url;
    }

    @Override
    public void setForm(IForm form) {
        this.form = form;
    }

    @Override
    public IForm getForm() {
        return form;
    }

    @Override
    public String execute() throws ServletException, IOException {
        String location = buildUrl();
        return "redirect:" + location;
    }

    public String buildUrl() {
        StringBuilder build = new StringBuilder();
        if (this.url == null) {
            return null;
        }
        build.append(this.url);

        int i = 0;
        for (String key : params.keySet()) {
            i++;
            build.append(i == 1 ? "?" : "&");
            build.append(key);
            build.append("=");
            String value = params.get(key);
            if (value != null) {
                build.append(encodeUTF8(value));
            }
        }
        return build.toString();
    }

    private String encodeUTF8(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    public String getUrl() {
        return url;
    }

    public RedirectPage setUrl(String url) {
        this.url = url;
        return this;
    }

    public RedirectPage put(String key, String value) {
        params.put(key, value);
        return this;
    }

}
