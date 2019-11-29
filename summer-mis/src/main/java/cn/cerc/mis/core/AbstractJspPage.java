package cn.cerc.mis.core;

import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import cn.cerc.core.TDate;
import cn.cerc.core.TDateTime;
import cn.cerc.core.Utils;
import cn.cerc.db.cache.Buffer;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.mis.language.R;
import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.HtmlContent;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.parts.UIContent;
import cn.cerc.ui.parts.UIDocument;
import cn.cerc.ui.parts.UIFooter;
import cn.cerc.ui.parts.UIHeader;
import cn.cerc.ui.parts.UIToolBar;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractJspPage extends UIComponent implements IPage {
    private String jspFile;
    private IForm form;
    private String browserCacheVersion;
    private List<String> cssFiles = new ArrayList<>();
    private List<String> scriptFiles = new ArrayList<>();
    private List<HtmlContent> scriptFunctions = new ArrayList<>();
    private List<HtmlContent> scriptCodes = new ArrayList<>();
    // 头部：广告+菜单
    private UIHeader header;
    // 主体: 控制区(可选)+内容+消息区
    private UIDocument document;
    // 工具面板：多页形式
    private UIToolBar toolBar;
    // 状态栏：快捷操作+按钮组
    private UIFooter footer;

    public AbstractJspPage() {
        super();
        ServerConfig config = ServerConfig.getInstance();
        this.browserCacheVersion = config.getProperty("browser.cache.version", "1.0.0.0");
    }

    @Override
    public final void setForm(IForm form) {
        this.form = form;
        if (form != null) {
            this.put("jspPage", this);
            // 为兼容而设计
            ServerConfig config = ServerConfig.getInstance();
            this.add("summer_js", config.getProperty("summer.js", "js/summer.js"));
            this.add("myapp_js", config.getProperty("myapp.js", "js/myapp.js"));
        }
    }

    @Override
    public final IForm getForm() {
        return form;
    }

    @Override
    public void addComponent(Component component) {
        if (component.getId() != null)
            this.put(component.getId(), component);
        super.addComponent(component);
    }

    @Override
    public String execute() throws ServletException, IOException {
        return this.getViewFile();
    }

    public final String getJspFile() {
        return jspFile;
    }

    public final void setJspFile(String jspFile) {
        this.jspFile = jspFile;
    }

    protected void put(String id, Object value) {
        getRequest().setAttribute(id, value);
    }

    public final String getMessage() {
        return form.getParam("message", null);
    }

    public final void setMessage(String message) {
        form.setParam("message", message);
    }

    public final String getViewFile() {
        String jspFile = this.getJspFile();
        if (getRequest() == null || jspFile == null)
            return jspFile;
        if (jspFile.indexOf(".jsp") == -1)
            return jspFile;

        String rootPath = String.format("/WEB-INF/%s/", Application.getAppConfig().getPathForms());
        String fileName = jspFile.substring(0, jspFile.indexOf(".jsp"));
        String extName = jspFile.substring(jspFile.indexOf(".jsp") + 1);

        // 检查是否存在 PC 专用版本的jsp文件
        String newFile = String.format("%s-%s.%s", fileName, "pc", extName);
        if (!this.getForm().getClient().isPhone() && fileExists(rootPath + newFile)) {
            // 检查是否存在相对应的语言版本
            String langCode = form == null ? Application.LangageDefault : R.getLanguage(form.getHandle());
            String langFile = String.format("%s-%s-%s.%s", fileName, "pc", langCode, extName);
            if (fileExists(rootPath + langFile))
                return langFile;
            return newFile;
        }

        // 检查是否存在相对应的语言版本
        String langCode = form == null ? Application.LangageDefault : R.getLanguage(form.getHandle());
        String langFile = String.format("%s-%s.%s", fileName, langCode, extName);
        if (fileExists(rootPath + langFile)) {
            return langFile;
        }

        // 发送消息
        String msg = form.getParam("message", "");
        getRequest().setAttribute("message", msg == null ? "" : msg.replaceAll("\r\n", "<br/>"));

        return jspFile;
    }

    protected boolean fileExists(String fileName) {
        URL url = AbstractJspPage.class.getClassLoader().getResource("");
        if (url == null)
            return false;
        String filepath = url.getPath();
        String appPath = filepath.substring(0, filepath.indexOf("/WEB-INF"));
        String file = appPath + fileName;
        File f = new File(file);
        return f.exists();
    }

    // 从请求或缓存读取数据
    public final String getValue(Buffer buff, String reqKey) {
        String result = getRequest().getParameter(reqKey);
        if (result == null) {
            String val = buff.getString(reqKey).replace("{}", "");
            if (Utils.isNumeric(val) && val.endsWith(".0"))
                result = val.substring(0, val.length() - 2);
            else
                result = val;
        } else {
            result = result.trim();
            buff.setField(reqKey, result);
        }
        this.add(reqKey, result);
        return result;
    }

    public final List<String> getCssFiles() {
        return cssFiles;
    }

    public final List<String> getScriptFiles() {
        return scriptFiles;
    }

    public final List<HtmlContent> getScriptCodes() {
        return scriptCodes;
    }

    public final void addCssFile(String file) {
        cssFiles.add(file);
    }

    public final void addScriptFile(String scriptFile) {
        scriptFiles.add(scriptFile);
    }

    public final void addScriptCode(HtmlContent scriptCode) {
        scriptCodes.add(scriptCode);
    }

    public void addScriptFunction(HtmlContent scriptCode) {
        scriptFunctions.add(scriptCode);
    }

    // 返回所有的样式定义，供jsp中使用 ${jspPage.css}调用
    public final HtmlWriter getCssHtml() {
        HtmlWriter html = new HtmlWriter();
        for (String file : cssFiles)
            html.println("<link href=\"%s?v=%s\" rel=\"stylesheet\">", file, browserCacheVersion);
        return html;
    }

    // 返回所有的脚本，供jsp中使用 ${jspPage.script}调用
    public final HtmlWriter getScriptHtml() {
        HtmlWriter html = new HtmlWriter();

        // 加入脚本文件
        for (String file : getScriptFiles()) {
            html.println("<script src=\"%s?v=%s\"></script>", file, browserCacheVersion);
        }
        // 加入脚本代码
        List<HtmlContent> scriptCode1 = getScriptCodes();
        if (scriptFunctions.size() > 0 || scriptCode1.size() > 0) {
            html.println("<script>");
            // 输出自定义的函数
            for (HtmlContent func : scriptFunctions) {
                func.output(html);
            }
            // 输出立即执行的代码
            if (scriptCode1.size() > 0) {
                html.println("$(function(){");
                for (HtmlContent func : scriptCodes) {
                    func.output(html);
                }
                html.println("});");
            }
            html.println("</script>");
        }
        return html;
    }

    public void add(String id, String value) {
        getRequest().setAttribute(id, value);
    }

    public void add(String id, boolean value) {
        put(id, value);
    }

    public void add(String id, double value) {
        put(id, value);
    }

    public void add(String id, int value) {
        put(id, value);
    }

    public void add(String id, List<?> value) {
        put(id, value);
    }

    public void add(String id, Map<?, ?> value) {
        put(id, value);
    }

    public void add(String id, DataSet value) {
        put(id, value);
    }

    public void add(String id, Record value) {
        put(id, value);
    }

    public void add(String id, TDate value) {
        put(id, value);
    }

    public void add(String id, TDateTime value) {
        put(id, value);
    }

    public void add(String id, UIComponent value) {
        put(id, value);
    }

    public UIFooter getFooter() {
        if (footer == null) {
            footer = new UIFooter(this);
            footer.setId("bottom");
            this.put("bottom", footer);
        }
        return footer;
    }

    public UIHeader getHeader() {
        if (header == null) {
            header = new UIHeader(this);
        }
        return header;
    }

    public UIDocument getDocument() {
        if (document == null) {
            document = new UIDocument(this);
        }
        return document;
    }

    public UIToolBar getToolBar() {
        if (toolBar == null) {
            toolBar = new UIToolBar(this);
        }
        return toolBar;
    }

    protected void outBody(PrintWriter out) {
        out.println("<body>");
        out.println(this.getHeader());
        out.println(this.getToolBar());
        out.println(this.getDocument());
        out.println(this.getFooter());
        if (getForm().getClient().isPhone()) {
            out.println("<span id='back-top' style='display: none'>顶部</span>");
            out.println("<span id='back-bottom' style='display: none'>底部</span>");
        }
        out.println("</body>");
    }

    public String getHtmlBody() {
        StringBuilder builder = new StringBuilder();
        builder.append("<body>");
        builder.append(this.getHeader());
        builder.append(this.getDocument());
        builder.append(this.getToolBar());
        builder.append(this.getFooter());
        if (getForm().getClient().isPhone()) {
            builder.append("<span id='back-top' style='display: none'>顶部</span>");
            builder.append("<span id='back-bottom' style='display: none'>底部</span>");
        }
        builder.append("</body>");
        return builder.toString();
    }

    public final UIContent getContent() {
        return this.getDocument().getContent();
    }

    protected void initCssFile() {
        ServerConfig config = ServerConfig.getInstance();
        this.addCssFile(config.getProperty("summer.css", "css/summer.css"));
        if (!getForm().getClient().isPhone()) {
            this.addCssFile(config.getProperty("summer-pc.css", "css/summer-pc.css"));
        }
    }

    protected void initJsFile() {
        ServerConfig config = ServerConfig.getInstance();
        this.addScriptFile(config.getProperty("jquery.js", "js/jquery.js"));
        this.addScriptFile(config.getProperty("summer.js", "js/summer.js"));
        this.addScriptFile(config.getProperty("myapp.js", "js/myapp.js"));
    }

}
