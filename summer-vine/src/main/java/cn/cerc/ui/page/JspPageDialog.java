package cn.cerc.ui.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import cn.cerc.core.ISession;
import cn.cerc.core.Utils;
import cn.cerc.mis.cdn.CDN;
import cn.cerc.mis.core.AbstractForm;
import cn.cerc.mis.core.AppClient;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.IForm;
import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.HtmlContent;
import cn.cerc.ui.core.MutiGrid;
import cn.cerc.ui.core.UrlRecord;
import cn.cerc.ui.grid.AbstractGrid;
import cn.cerc.ui.grid.MutiPage;
import cn.cerc.ui.mvc.IMenuBar;
import cn.cerc.ui.other.OperaPages;
import cn.cerc.ui.parts.RightMenus;
import cn.cerc.ui.parts.UIFooter;
import cn.cerc.ui.parts.UIToolbar;

public class JspPageDialog extends JspPage {
    private boolean showMenus = true; // 是否显示主菜单
    private MutiPage pages;
    // 工具面板：多页形式
    private UIToolbar toolBar;
    private UIFooter footer;
    //
    // FIXME 此处调用不合理，为保障编译通过先保留 2021/3/14
    private List<HtmlContent> scriptCodes = new ArrayList<>();
    // FIXME 此处调用不合理，为保障编译通过先保留 2021/3/14
    private List<String> jsFiles = new ArrayList<>();
    // FIXME 此处调用不合理，为保障编译通过先保留 2021/3/14
    private List<String> cssFiles = new ArrayList<>();

    public JspPageDialog(IForm form) {
        super();
        setForm(form);
    }

    public void addExportFile(String service, String key) {
        if (AppClient.ee.equals(this.getForm().getClient().getDevice())) {
            ExportFile item = new ExportFile(service, key);
            this.put("export", item);
        }
    }

    @Override
    public String execute() throws ServletException, IOException {
        IForm form = this.getForm();
        HttpServletRequest request = form.getRequest();
        ISession session = form.getHandle().getSession();
        request.setAttribute("passport", session.logon());
        request.setAttribute("logon", session.logon());
        if (session.logon()) {
            List<UrlRecord> rightMenus = getHeader().getRightMenus();
            RightMenus menus = Application.getBean(RightMenus.class, "RightMenus", "rightMenus");
            menus.setHandle(form.getHandle());
            for (IMenuBar item : menus.getItems()) {
                item.enrollMenu(form, rightMenus);
            }
        } else {
            getHeader().getHomePage().setSite(config.getString(Application.FORM_WELCOME, "welcome"));
        }
        // 设置首页
        request.setAttribute("_showMenu_", "true".equals(form.getParam("showMenus", "true")));
        // 系统通知消息
        if (request.getAttribute("message") == null) {
            request.setAttribute("message", "");
        }

        if (form instanceof AbstractForm) {
            if (this.isShowMenus()) {
                this.getHeader().initHeader();

                this.getRequest().setAttribute("logoSrc", this.getHeader().getLogoSrc());
                this.getRequest().setAttribute("welcomeLanguage", this.getHeader().getWelcome());
                if (Utils.isNotEmpty(this.getHeader().getUserName())) {
                    this.getRequest().setAttribute("exitSystem", this.getHeader().getExitSystem());
                    this.getRequest().setAttribute("userName", this.getHeader().getUserName());
                    this.getRequest().setAttribute("currentUser", this.getHeader().getCurrentUser());
                }
            }
        }
        String msg = form.getParam("message", "");
        request.setAttribute("msg", msg == null ? "" : msg.replaceAll("\r\n", "<br/>"));
        request.setAttribute("formno", form.getParam("formNo", "000"));
        request.setAttribute("form", form);

        // 添加分页控制
        Component operaPages = null;
        if (pages != null) {
            this.put("pages", pages);
            operaPages = new OperaPages(this.getToolBar(), this.getForm(), pages);
            this.put("_operaPages_", operaPages);
        }

        // 输出jsp模版
        return this.getViewFile();
    }

    public UIToolbar getToolBar() {
        if (toolBar == null) {
            toolBar = new UIToolbar(this);
        }
        return toolBar;
    }

    public UIFooter getFooter() {
        if (footer == null) {
            footer = new UIFooter(this);
        }
        return footer;
    }

    public void installAdvertisement() {
        super.put("_showAd_", this.getHeader().getAdvertisement());
    }

    public boolean isShowMenus() {
        return showMenus;
    }

    public void setShowMenus(boolean showMenus) {
        this.showMenus = showMenus;
    }

    public void add(String id, MutiGrid<?> grid) {
        put(id, grid.getList());
        pages = grid.getPages();
    }

    public void add(String id, AbstractGrid grid) {
        put(id, grid);
        pages = grid.getPages();
    }

    @Deprecated
    public final List<String> getJsFiles() {
        return jsFiles;
    }

    @Deprecated
    public final void addScriptFile(String file) {
        file = CDN.get(file);
        jsFiles.add(file);
    }

    @Deprecated
    public final List<HtmlContent> getScriptCodes() {
        return scriptCodes;
    }

    @Deprecated
    public final void addScriptCode(HtmlContent scriptCode) {
        scriptCodes.add(scriptCode);
    }

    @Deprecated
    public final List<String> getCssFiles() {
        return cssFiles;
    }

    @Deprecated
    public final void addCssFile(String file) {
        file = CDN.get(file);
        cssFiles.add(file);
    }

}
