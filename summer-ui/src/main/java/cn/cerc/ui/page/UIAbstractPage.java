package cn.cerc.ui.page;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.IClient;
import cn.cerc.mis.core.IForm;
import cn.cerc.mis.core.IPage;
import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IOriginOwner;
import cn.cerc.ui.core.UIOriginComponent;
import cn.cerc.ui.parts.UIComponent;

public abstract class UIAbstractPage extends UIComponent implements IPage, IOriginOwner {
    private List<String> cssFiles = new ArrayList<>();
    private List<String> jsFiles = new ArrayList<>();
    private UIComponent header; // 头部区域，剩下的均为下部区域
    private UIComponent aside; // 左部区域，剩下的均为右部区域
    private UIComponent menuPath; // （中间右边上方）菜单路径
    private UIComponent notice; // （中间右边上方）通知区域
    private UIComponent content; // （中间右边）主内容区域
    private UIComponent statusBar; // （中间右边下方）下方状态条
    private UIComponent footer; // 底部区域
    private IForm form;
    private Object origin;

    public UIAbstractPage(IForm form) {
        this.setForm(form);
        if (form != null) {
            this.origin = form;
            this.initComponents(form.getClient());
        }
    }

    /**
     * 在此函数中，实现对header\aside\content\footer 的初始化
     * 
     * @param client 代表当前运行的客户端环境
     */
    public abstract void initComponents(IClient client);

    @Override
    public final String execute() throws ServletException, IOException {
        PrintWriter out = getResponse().getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println(String.format("<title>%s</title>", getForm().getName()));
        out.println("<meta name=\"referrer\" content=\"no-referrer\" />");
        out.println("<meta name=\"format-detection\" content=\"telephone=no\" />");
        out.println("<meta name=\"format-detection\" content=\"email=no\" />");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>");
        out.println("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=9; IE=8; IE=7;\"/>");
        out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1, user-scalable=0\"/>");
        // 加入脚本文件
        String device = this.getForm().getClient().isPhone() ? "phone" : "pc";
        String staticPath = Application.getStaticPath();
        for (String file : getJsFiles()) {
            String[] args = file.split("\\.");
            out.println(String.format("<script src=\"%s%s-%s.%s\"></script>", staticPath, args[0], device, args[1]));
        }
        // 加入样式文件
        for (String file : cssFiles) {
            String[] args = file.split("\\.");
            out.println(String.format("<link href=\"%s%s-%s.%s\" rel=\"stylesheet\">", staticPath, args[0], device,
                    args[1]));
        }
        

        out.println("</head>");
        out.println("<body>");

        // 头部区域
        if (header != null) {
            out.println("<header>");
            out.println(header);
            out.println("</header>");
        }
        // 下部区域
        out.println("<main>");
        // 左部区域
        if (aside != null) {
            out.println("<aside>");
            out.println(aside);
            out.println("</aside>");
        }
        // 右侧区域
        out.println("<article>");
        if (menuPath != null) {
            out.print("<div class='menuPath'>");
            out.print(menuPath);
            out.println("</div>");
        }
        if (notice != null) {
            out.print("<div class='notice'>");
            out.print(notice);
            out.println("</div>");
        }
        out.println("<content>");
        if (content != null)
            out.println(content);
        out.println("</content>");
        // （中间右边下方）下方状态条
        if (statusBar != null) {
            out.println("<div class='statusBar'>");
            out.println(statusBar);
            out.println("</div>");
        }
        out.println("</article>");
        out.println("</main>");

        // 底部区域
        if (footer != null) {
            out.println("<footer>");
            out.println(footer);
            out.println("</footer>");
        }
        out.println("</body>");
        out.println("</html>");
        return null;
    }

    @Override
    public void output(HtmlWriter html) {
        for (Component component : this.getComponents()) {
            if (component instanceof UIComponent) {
                ((UIComponent) component).output(html);
            }
        }
    }

    @Override
    public IForm getForm() {
        return form;
    }

    @Override
    public void setForm(IForm form) {
        this.form = form;
    }

    public final List<String> getJsFiles() {
        return jsFiles;
    }

    public final void addScriptFile(String file) {
        jsFiles.add(file);
    }

    public final List<String> getCssFiles() {
        return cssFiles;
    }

    public final void addCssFile(String file) {
        cssFiles.add(file);
    }

    public UIComponent getHeader() {
        if (header == null)
            header = new UIOriginComponent(this);
        return header;
    }

    public UIComponent setHeader(UIComponent header) {
        this.header = header;
        return this;
    }

    public UIComponent getAside() {
        if (aside == null) {
            aside = new UIOriginComponent(this);
        }
        return aside;
    }

    public UIComponent setAside(UIComponent aside) {
        this.aside = aside;
        return this;
    }

    public UIComponent getMenuPath() {
        if (menuPath == null)
            menuPath = new UIOriginComponent(this);
        return menuPath;
    }

    public UIComponent setMenuPath(UIComponent menuPath) {
        this.menuPath = menuPath;
        return this;
    }

    public UIComponent getNotice() {
        if (notice == null)
            notice = new UIOriginComponent(this);
        return notice;
    }

    public UIComponent setNotice(UIComponent notice) {
        this.notice = notice;
        return this;
    }

    public UIComponent getContent() {
        if (content == null)
            content = new UIOriginComponent(this);
        return content;
    }

    public UIComponent setContent(UIComponent content) {
        this.content = content;
        return this;
    }

    public UIComponent getStatusBar() {
        if (statusBar == null)
            statusBar = new UIOriginComponent(this);
        return statusBar;
    }

    public UIComponent setStatusBar(UIComponent statusBar) {
        this.statusBar = statusBar;
        return this;
    }

    public UIComponent getFooter() {
        if (footer == null)
            footer = new UIOriginComponent(this);
        return footer;
    }

    public UIComponent setFooter(UIComponent footer) {
        this.footer = footer;
        return this;
    }

    @Override
    public final void setOrigin(Object orgin) {
        this.origin = orgin;
    }

    @Override
    public final Object getOrigin() {
        return origin;
    }

}
