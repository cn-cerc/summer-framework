package cn.cerc.ui.page;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import cn.cerc.core.ClassResource;
import cn.cerc.core.Utils;
import cn.cerc.mis.cdn.CDN;
import cn.cerc.mis.core.AbstractForm;
import cn.cerc.mis.core.IForm;
import cn.cerc.mis.language.R;
import cn.cerc.ui.SummerUI;
import cn.cerc.ui.core.HtmlContent;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.menu.MenuList;
import cn.cerc.ui.menu.MenuModel;
import cn.cerc.ui.mvc.AbstractPage;
import cn.cerc.ui.mvc.StartForms;
import cn.cerc.ui.parts.UIContent;
import cn.cerc.ui.parts.UIDocument;
import cn.cerc.ui.parts.UIFooter;
import cn.cerc.ui.parts.UIHeader;
import cn.cerc.ui.parts.UISheetHelp;
import cn.cerc.ui.parts.UIToolbar;

public abstract class UIPage extends AbstractPage {
    private final ClassResource res = new ClassResource(this, SummerUI.ID);

    private List<String> cssFiles = new ArrayList<>();
    private List<String> jsFiles = new ArrayList<>();
    private List<HtmlContent> scriptFunctions = new ArrayList<>();
    private List<HtmlContent> scriptCodes = new ArrayList<>();
    // 头部：广告+菜单
    private UIHeader header;
    // 主体: 控制区(可选)+内容+消息区
    private UIDocument document;
    // 工具面板：多页形式
    private UIToolbar toolBar;
    // 状态栏：快捷操作+按钮组
    private UIFooter footer;
    // FIXME 此处调用不合理，为保障编译通过先保留 2021/3/14
    private String jspFile;

    @Override
    public final String execute() throws ServletException, IOException {
        getResponse().setCharacterEncoding(StandardCharsets.UTF_8.name());
        writeHtml(getResponse().getWriter());
        return null;
    }

    /**
     * 输出html代码
     * 
     * @param out
     */
    protected abstract void writeHtml(PrintWriter out);

    public final List<String> getJsFiles() {
        return jsFiles;
    }

    public final List<HtmlContent> getScriptCodes() {
        return scriptCodes;
    }

    public final List<String> getCssFiles() {
        return cssFiles;
    }

    public final void addCssFile(String file) {
        file = CDN.get(file);
        cssFiles.add(file);
    }

    public final void addOnlineScript(String address) {
        jsFiles.add(address);
    }

    public final void addScriptFile(String file) {
        file = CDN.get(file);
        jsFiles.add(file);
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
        for (String file : cssFiles) {
            html.println("<link href=\"%s\" rel=\"stylesheet\">", file);
        }
        return html;
    }

    // 返回所有的脚本，供jsp中使用 ${jspPage.script}调用
    public final HtmlWriter getScriptHtml() {
        HtmlWriter html = new HtmlWriter();

        // 加入脚本文件
        for (String file : getJsFiles()) {
            html.println("<script src=\"%s\"></script>", file);
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

    public UIToolbar getToolBar() {
        if (toolBar == null) {
            toolBar = new UIToolbar(this);
        }
        return toolBar;
    }

    /**
     * 获取指定菜单的描述和停用时间
     */
    public UIToolbar getToolBar(AbstractForm handle) {
        if (toolBar == null) {
            toolBar = new UIToolbar(this);
        }
        String menuCode = StartForms.getRequestCode(handle.getRequest());
        String[] params = menuCode.split("\\.");
        String formId = params[0];
        if (!menuCode.equals(formId)) {
            return toolBar;
        }

        // 输出菜单信息
        MenuModel item = MenuList.create(handle).get(formId);
        if (item == null) {
            return toolBar;
        }
        if (Utils.isNotEmpty(item.getRemark())) {
            UISheetHelp section = new UISheetHelp(toolBar);
            section.setCaption(res.getString(1, "菜单描述"));
            section.addLine("%s", item.getRemark());
        }
        if (Utils.isNotEmpty(item.getDeadline())) {
            UISheetHelp section = new UISheetHelp(toolBar);
            section.setCaption(res.getString(2, "停用时间"));
            section.addLine("<font color='red'>%s</font>", item.getDeadline());
        }
        return toolBar;
    }

    protected void writeHead(PrintWriter out) {
        IForm form = getForm();

        String menuCode = StartForms.getRequestCode(this.getForm().getRequest());
        String[] params = menuCode.split("\\.");
        String formId = params[0];
        if (Utils.isNotEmpty(this.getForm().getName())) {
            out.printf("<title>%s</title>", R.asString(form.getHandle(), this.getForm().getName()));
        } else {
            out.printf("<title>%s</title>",
                    R.asString(form.getHandle(), MenuList.create(this.getForm().getHandle()).getName(formId)));
        }

        // 所有的请求都不发送 referrer
        out.println("<meta name=\"referrer\" content=\"no-referrer\" />");
        out.println("<meta name=\"format-detection\" content=\"telephone=no\" />");
        out.println("<meta name=\"format-detection\" content=\"email=no\" />");
        out.printf("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>");
        out.println("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=9; IE=8; IE=7;\"/>");
        out.printf(
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1, user-scalable=0\"/>");
        out.print(this.getCssHtml());
        out.print(getScriptHtml());
        out.println("<script>");
        out.println("var Application = new TApplication();");
        out.printf("Application.device = '%s';\n", form.getClient().getDevice());
        out.printf("Application.bottom = '%s';\n", getFooter().getId());
        String msg = form.getParam("message", "");
        msg = msg == null ? "" : msg.replaceAll("\r\n", "<br/>");
        out.printf("Application.message = '%s';\n", msg);
        out.println("$(document).ready(function() {");
        out.println("Application.init();");
        out.println("});");
        out.println("</script>");
    }

    @Deprecated // 请改使用writeBody
    protected void outBody(PrintWriter out) {
        out.println("<body>");
        writeBody(out);
        out.println("</body>");
    }

    protected void writeBody(PrintWriter out) {
        out.println(this.getHeader());
        out.println(this.getToolBar());
        out.println(this.getDocument());
        out.println(this.getFooter());
        if (getForm().getClient().isPhone()) {
            out.println(String.format("<span id='back-top' style='display: none'>%s</span>", res.getString(3, "顶部")));
            out.println(
                    String.format("<span id='back-bottom' style='display: none'>%s</span>", res.getString(4, "底部")));
        }
    }

    public String getHtmlBody() {
        StringBuilder builder = new StringBuilder();
        builder.append("<body>");
        builder.append(this.getHeader());
        builder.append(this.getDocument());
        builder.append(this.getToolBar());
        builder.append(this.getFooter());
        if (getForm().getClient().isPhone()) {
            builder.append(
                    String.format("<span id='back-top' style='display: none'>%s</span>", res.getString(3, "顶部")));
            builder.append(
                    String.format("<span id='back-bottom' style='display: none'>%s</span>", res.getString(4, "底部")));
        }
        builder.append("</body>");
        return builder.toString();
    }

    public final UIContent getContent() {
        return this.getDocument().getContent();
    }

    protected void initCssFile() {
        this.addCssFile(config.getString("summer.css", "css/summer.css"));
        if (!getForm().getClient().isPhone()) {
            this.addCssFile(config.getString("summer-pc.css", "css/summer-pc.css"));
        }
    }

    protected void initJsFile() {
        this.addScriptFile(config.getString("jquery.js", "js/jquery.js"));
        this.addScriptFile(config.getString("summer.js", "js/summer.js"));
        this.addScriptFile(config.getString("myapp.js", "js/myapp.js"));
    }

    @Deprecated
    public String getViewFile() {
        // FIXME 不应该出现此处的调用！
        throw new RuntimeException("不应该出现此处的调用！");
    }
    
    @Deprecated
    public final String getJspFile() {
        return jspFile;
    }

    @Deprecated
    public final void setJspFile(String jspFile) {
        this.jspFile = jspFile;
    }

}
