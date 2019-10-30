package cn.cerc.mis.language;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.db.core.ServerConfig;
import cn.cerc.mis.core.Application;
import cn.cerc.core.IHandle;

public class ResourceJstl extends SimpleTagSupport {
    private static final Logger log = LoggerFactory.getLogger(ResourceJstl.class);
    private String toId = null;
    private static Map<String, ResourceBuffer> items = new HashMap<>();

    @Override
    public void doTag() throws JspException, IOException {
        JspFragment jf = this.getJspBody();
        String text = "";
        try {
            HttpServletRequest request = (HttpServletRequest) ((PageContext) this.getJspContext()).getRequest();
            try {
                text = getString(request, jf);
            } catch (Exception e) {
                String uri = request.getRequestURI();
                log.error("key为空，uri：" + uri);
                text = "file error";
            }
        } finally {
            if (this.toId == null) {
                this.getJspContext().getOut().write(text);
            } else {
                this.getJspContext().setAttribute(this.toId, text);
            }
            super.doTag();
        }
    }

    private String getString(HttpServletRequest request, JspFragment jf) throws JspException, IOException {
        IHandle handle = (IHandle) request.getAttribute("myappHandle");
        if (handle == null) {
            log.error("handle is null");
            return "handle is null";
        }

        String text = "";
        StringWriter sw = new StringWriter();
        jf.invoke(sw);
        text = sw.toString();
        Object temp = handle.getProperty(Application.deviceLanguage);
        String lang = (temp == null || "".equals(temp)) ? Application.getLangage() : (String) temp;
        if ("cn".equals(lang)) {
            return text;
        }

        // 取其它语言的字符
        ResourceBuffer rb = items.get(lang);
        if (rb == null) {
            rb = new ResourceBuffer(lang);
            items.put(lang, rb);
        }

        // 取得到值，则直接返回
        String result = rb.get(handle, text);
        if (result != null) {
            if (!"".equals(result)) {
                return result;
            }
        }

        // 取不到值，但当前是英文时，则原样返回
        if ("en".equals(lang)) {
            if (ServerConfig.getInstance().isDebug()) {
                return lang + ":" + text;
            } else {
                return text;
            }
        }

        // 如果是其它语言，则取英文的默认值
        ResourceBuffer en = items.get("en");
        result = en.get(handle, text);
        if (result != null && !"".equals(result)) {
            return result;
        }

        if (ServerConfig.getInstance().isDebug()) {
            return lang + ":" + text;
        } else {
            return text;
        }
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public static void clearBuffer() {
        for (String lang : items.keySet()) {
            items.get(lang).clear();
        }
    }
}
