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
import cn.cerc.ui.core.UICustomComponent;
import cn.cerc.ui.core.UIOriginComponent;
import cn.cerc.ui.parts.UIComponent;

public abstract class UIAbstractPage extends UIComponent implements IPage, IOriginOwner {
    private List<String> cssFiles = new ArrayList<>();
    private List<String> jsFiles = new ArrayList<>();
    private UIComponent header; // 头部区域，剩下的均为下部区域
    private UIComponent aside; // 左部区域，剩下的均为右部区域
    @Deprecated
    private UIComponent menuPath; // （中间右边上方）菜单路径
    @Deprecated
    private UIComponent notice; // （中间右边上方）通知区域
    private UIComponent frontPanel; // （中间右边上方）控制区域（Web显示固定）
    private UIComponent content; // （中间右边）主内容区域
    private UIComponent footer; // （中间右边）尾部区部（Web显示固定）
    @Deprecated
    private UIComponent statusBar; // （中间右边下方）下方状态条（Web显示固定）
    private UIComponent address; // 底部区域
    private IForm form;
    private Object origin;
    private DefineContent defineHead;

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
        out.println(
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1, user-scalable=0\"/>");
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

        if (defineHead != null) {
            UIComponent content = new UICustomComponent();
            defineHead.execute(this, content);
            out.print(content.toString());
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
        // （中间右边上方）控制区域（Web显示固定）
        if (frontPanel != null) {
            out.print("<div class='frontPanel'>");
            out.print(frontPanel);
            out.println("</div>");
        }
        // （中间右边）主内容区域
        out.println("<content>");
        if (content != null)
            out.println(content);
        out.println("</content>");
        // （中间右边下方）下方状态条
        if (!isPhone()) {
            if (footer != null) {
                out.println("<footer>");
                out.println(footer);
                out.println("</footer>");
            }
        }
        out.println("</article>");
        out.println("</main>");

        if (address != null) {
            out.println("<address>");
            out.println(address);
            out.println("</address>");
        }
        if (isPhone()) {
            if (footer != null) {
                out.println("<footer>");
                out.println(footer);
                out.println("</footer>");
            }
        }
        // 底部区域
        out.println("</body>");
        out.println("</html>");
        return null;
    }

    public interface DefineContent {
        void execute(UIAbstractPage sender, UIComponent content);
    }

    public void DefineHead(DefineContent defineHead) {
        this.defineHead = defineHead;
    }

    private boolean isPhone() {
        if (this.getOrigin() instanceof IForm) {
            IForm form = (IForm) this.getOrigin();
            return form.getClient().isPhone();
        } else {
            return false;
        }
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

    public UIComponent getAside() {
        if (aside == null) {
            aside = new UIOriginComponent(this);
        }
        return aside;
    }

    @Deprecated
    // 请改使用 getControls
    public UIComponent getMenuPath() {
        if (menuPath == null)
            menuPath = new UIOriginComponent(this.getFrontPanel());
        return menuPath;
    }

    @Deprecated
    // 请改使用 getControls
    public UIComponent getNotice() {
        if (notice == null)
            notice = new UIOriginComponent(this.getFrontPanel());
        return notice;
    }

    public UIComponent getContent() {
        if (content == null)
            content = new UIOriginComponent(this);
        return content;
    }

    public UIComponent getFrontPanel() {
        if (frontPanel == null)
            frontPanel = new UIOriginComponent(this);
        return frontPanel;
    }

    @Deprecated
    // 请改使用 getFooter
    public UIComponent getStatusBar() {
        if (statusBar == null)
            statusBar = new UIOriginComponent(this);
        return statusBar;
    }

    public UIComponent getFooter() {
        if (footer == null)
            footer = new UIOriginComponent(this);
        return footer;
    }

    public UIComponent getAddress() {
        if (address == null)
            address = new UIOriginComponent(this);
        return address;
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
