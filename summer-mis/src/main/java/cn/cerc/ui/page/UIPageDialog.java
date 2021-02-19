package cn.cerc.ui.page;

import static cn.cerc.mis.core.ClientDevice.device_ee;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import cn.cerc.mis.core.AbstractForm;
import cn.cerc.mis.core.AbstractJspPage;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.HandleDefault;
import cn.cerc.mis.core.IForm;
import cn.cerc.mis.page.ExportFile;
import cn.cerc.mis.page.IMenuBar;
import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.MutiGrid;
import cn.cerc.ui.core.UrlRecord;
import cn.cerc.ui.grid.AbstractGrid;
import cn.cerc.ui.grid.MutiPage;
import cn.cerc.ui.other.OperaPages;
import cn.cerc.ui.parts.RightMenus;

public class UIPageDialog extends AbstractJspPage {
    private boolean showMenus = true; // 是否显示主菜单
    private MutiPage pages;

    public UIPageDialog(IForm form) {
        super();
        setForm(form);
    }

    public void addExportFile(String service, String key) {
        if (device_ee.equals(this.getForm().getClient().getDevice())) {
            ExportFile item = new ExportFile(service, key);
            this.put("export", item);
        }
    }

    @Override
    public String execute() throws ServletException, IOException {
        IForm form = this.getForm();
        HttpServletRequest request = form.getRequest();
        HandleDefault sess = (HandleDefault) form.getHandle().getProperty(null);
        request.setAttribute("passport", sess.logon());
        request.setAttribute("logon", sess.logon());
        if (sess.logon()) {
            List<UrlRecord> rightMenus = getHeader().getRightMenus();
            RightMenus menus = Application.getBean(RightMenus.class, "RightMenus", "rightMenus");
            menus.setHandle(form.getHandle());
            for (IMenuBar item : menus.getItems())
                item.enrollMenu(form, rightMenus);
        } else {
            getHeader().getHomePage().setSite(Application.getAppConfig().getFormWelcome());
        }
        // 设置首页
        request.setAttribute("_showMenu_", "true".equals(form.getParam("showMenus", "true")));
        // 系统通知消息
        if (request.getAttribute("message") == null)
            request.setAttribute("message", "");

        if (form instanceof AbstractForm) {
            if (this.isShowMenus())
                this.getHeader().initHeader();
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

    public void installAdvertisement() {
        super.put("_showAd_", this.getHeader().getAdvertisement());
    }

    public boolean isShowMenus() {
        return showMenus;
    }

    public void setShowMenus(boolean showMenus) {
        // this.setParam("showMenus", "false");
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
}
