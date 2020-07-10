package cn.cerc.ui.page;

import cn.cerc.core.DataSet;
import cn.cerc.core.Utils;
import cn.cerc.mis.core.AbstractForm;
import cn.cerc.mis.core.AbstractJspPage;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.ClientDevice;
import cn.cerc.mis.core.HandleDefault;
import cn.cerc.mis.core.IForm;
import cn.cerc.mis.core.StartForms;
import cn.cerc.mis.language.R;
import cn.cerc.mis.other.MemoryBuffer;
import cn.cerc.mis.page.ExportFile;
import cn.cerc.mis.page.IMenuBar;
import cn.cerc.mis.rds.PassportRecord;
import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.HtmlContent;
import cn.cerc.ui.core.UrlRecord;
import cn.cerc.ui.grid.AbstractGrid;
import cn.cerc.ui.grid.GridFactory;
import cn.cerc.ui.grid.MutiPage;
import cn.cerc.ui.menu.MenuList;
import cn.cerc.ui.other.OperaPages;
import cn.cerc.ui.parts.RightMenus;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.parts.UIFormHorizontal;
import cn.cerc.ui.parts.UIFormVertical;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * 主体子页面
 *
 * @author 张弓
 */
public class UIPageSearch extends AbstractJspPage {
    private MutiPage pages;
    private String searchWaitingId = "";

    public UIPageSearch(IForm form) {
        super();
        setForm(form);
        initCssFile();
        initJsFile();
    }

    public void addExportFile(String service, String key) {
        if (ClientDevice.APP_DEVICE_EE.equals(this.getForm().getClient().getDevice())) {
            ExportFile item = new ExportFile(service, key);
            this.put("export", item);
        }
    }

    @Override
    public String execute() throws ServletException, IOException {
        HttpServletRequest request = getRequest();

        // 添加分页控制
        Component operaPages = null;
        if (pages != null) {
            this.put("pages", pages);
            operaPages = new OperaPages(this.getToolBar(), this.getForm(), pages);
            this.put("_operaPages_", operaPages);
        }
        IForm form = this.getForm();
        HandleDefault sess = (HandleDefault) form.getHandle().getProperty(null);
        if (sess.logon()) {
            List<UrlRecord> rightMenus = getHeader().getRightMenus();
            RightMenus menus = Application.getBean(RightMenus.class, "RightMenus", "rightMenus");
            menus.setHandle(form.getHandle());
            for (IMenuBar item : menus.getItems()) {
                item.enrollMenu(form, rightMenus);
            }
        } else {
            getHeader().getHomePage().setSite(Application.getAppConfig().getFormWelcome());
        }

        // 系统通知消息
        Component content = this.getContent();
        if (form instanceof AbstractForm) {
            this.getHeader().initHeader();
            request.setAttribute(content.getId(), content);
            for (Component component : content.getComponents()) {
                request.setAttribute(component.getId(), component);
            }
        }

        // 开始输出
        StringBuilder builder = new StringBuilder();
        PrintWriter out = getResponse().getWriter();
        builder.append("<!DOCTYPE html>");
        builder.append("<html>");
        builder.append("<head>");

        String menuCode = StartForms.getRequestCode(this.getForm().getRequest());
        String[] params = menuCode.split("\\.");
        String formId = params[0];
        if (Utils.isNotEmpty(this.getForm().getName())) {
            builder.append(String.format("<title>%s</title>", R.asString(form.getHandle(), this.getForm().getName())));
        } else {
            builder.append(String.format("<title>%s</title>", R.asString(form.getHandle(), MenuList.create(this.getForm().getHandle()).getName(formId))));
        }

        // 所有的请求都不发送 referrer
        builder.append("<meta name=\"referrer\" content=\"no-referrer\" />");
        builder.append("<meta name=\"format-detection\" content=\"telephone=no\" />");
        builder.append("<meta name=\"format-detection\" content=\"email=no\" />");
        builder.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n");
        builder.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=9; IE=8; IE=7;\"/>");
        builder.append(
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1, user-scalable=0\"/>");
        builder.append(this.getCssHtml());
        builder.append(getScriptHtml());
        builder.append("<script>");
        builder.append("var Application = new TApplication();");
        builder.append(String.format("Application.device = '%s';", form.getClient().getDevice()));

        builder.append(String.format("Application.bottom = '%s';", this.getFooter().getId()));

        String msg = form.getParam("message", "");
        msg = msg == null ? "" : msg.replaceAll("\r\n", "<br/>");
        builder.append(String.format("Application.message = '%s';", msg.replace("'", "\\'")));
        builder.append(String.format("Application.searchFormId = '%s';", this.searchWaitingId));
        builder.append("$(document).ready(function() {");
        builder.append("Application.init();");
        builder.append("});");
        builder.append("</script>");
        builder.append("</head>");
        outBody(builder);
        builder.append("</html>");
        out.print(builder.toString());
        return null;
    }

    public void appendContent(HtmlContent content) {
        this.getDocument().getContent().append(content);
    }

    public UIFormHorizontal createSearch(MemoryBuffer buff) {
        UIFormHorizontal search = new UIFormHorizontal(this.getDocument().getControl(), this.getRequest());
        search.setBuffer(buff);
        this.setSearchWaitingId(search.getId());
        return search;
    }

    public AbstractGrid createGrid(UIComponent owner, DataSet dataSet) {
        AbstractGrid grid = GridFactory.build(this.getForm(), owner);
        grid.setDataSet(dataSet);
        pages = grid.getPages();
        return grid;
    }

    public String getSearchWaitingId() {
        return searchWaitingId;
    }

    public void setSearchWaitingId(String searchWaitingId) {
        this.searchWaitingId = searchWaitingId;
    }

    public void add(String id, AbstractGrid grid) {
        getRequest().setAttribute(id, grid);
        pages = grid.getPages();
    }

    public void add(String id, UIFormHorizontal value) {
        put(id, value);
    }

    public void add(String id, UIFormVertical value) {
        put(id, value);
    }

    public void add(String id, ExportFile value) {
        put(id, value);
    }

    public void add(String id, PassportRecord value) {
        put(id, value);
    }
}
