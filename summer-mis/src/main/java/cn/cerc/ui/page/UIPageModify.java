package cn.cerc.ui.page;

import static cn.cerc.mis.core.ClientDevice.device_ee;

import java.io.IOException;
import java.io.PrintWriter;
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
import cn.cerc.ui.core.UrlRecord;
import cn.cerc.ui.parts.RightMenus;
import cn.cerc.ui.parts.UIFormVertical;

/**
 * 主体子页面(公用)
 *
 * @author 张弓
 */
public class UIPageModify extends AbstractJspPage {
    private String searchWaitingId = "";
    private Component body;

    public UIPageModify(IForm form) {
        super();
        setForm(form);
        initCssFile();
        initJsFile();
        if (!this.getForm().getClient().isPhone()) {
            this.getHeader().getAdvertisement();
        }
    }

    public void addExportFile(String service, String key) {
        if (device_ee.equals(this.getForm().getClient().getDevice())) {
            ExportFile item = new ExportFile(service, key);
            this.put("export", item);
        }
    }

    @Override
    public String execute() throws ServletException, IOException {
        HttpServletRequest request = getRequest();

        IForm form = this.getForm();
        HandleDefault sess = (HandleDefault) form.getHandle().getProperty(null);
        if (sess.logon()) {
            List<UrlRecord> rightMenus = getHeader().getRightMenus();
            RightMenus menus = Application.getBean(RightMenus.class, "RightMenus", "rightMenus");
            menus.setHandle(form.getHandle());
            for (IMenuBar item : menus.getItems())
                item.enrollMenu(form, rightMenus);
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
        PrintWriter out = getResponse().getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.printf("<title>%s</title>\n", this.getForm().getTitle());

        // 所有的请求都不发送 referrer
        out.println("<meta name=\"referrer\" content=\"no-referrer\" />");
        out.println("<meta name=\"format-detection\" content=\"telephone=no\" />");
        out.println("<meta name=\"format-detection\" content=\"email=no\" />");
        out.printf("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n");
        out.println("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=9; IE=8; IE=7;\"/>");
        out.printf(
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1, user-scalable=0\"/>\n");
        out.print(this.getCssHtml());
        out.print(getScriptHtml());
        out.println("<script>");
        out.println("var Application = new TApplication();");
        out.printf("Application.device = '%s';\n", form.getClient().getDevice());
        out.printf("Application.bottom = '%s';\n", this.getFooter().getId());
        String msg = form.getParam("message", "");
        msg = msg == null ? "" : msg.replaceAll("\r\n", "<br/>");
        out.printf("Application.message = '%s';\n", msg);
        out.printf("Application.searchFormId = '%s';\n", this.searchWaitingId);
        out.println("$(document).ready(function() {");
        out.println("Application.init();");
        out.println("});");
        out.println("</script>");
        out.println("</head>");
        outBody(out);
        out.println("</html>");
        return null;
    }

    public UIFormVertical createForm() {
        UIFormVertical form = new UIFormVertical(this.getDocument().getContent());
        form.setId("search");
        put("search", form);
        return form;
    }

    public Component getBody() {
        if (body == null) {
            body = new Component();
            body.setOwner(this.getDocument().getContent());
            body.setId("search");
        }
        return body;
    }

    public String getSearchWaitingId() {
        return searchWaitingId;
    }

    public void setSearchWaitingId(String searchWaitingId) {
        this.searchWaitingId = searchWaitingId;
    }

}
