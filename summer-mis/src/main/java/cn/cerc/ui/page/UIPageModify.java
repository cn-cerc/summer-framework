package cn.cerc.ui.page;

import cn.cerc.core.Utils;
import cn.cerc.mis.core.AbstractForm;
import cn.cerc.mis.core.AbstractJspPage;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.ClientDevice;
import cn.cerc.mis.core.HandleDefault;
import cn.cerc.mis.core.IForm;
import cn.cerc.mis.core.StartForms;
import cn.cerc.mis.language.R;
import cn.cerc.mis.page.ExportFile;
import cn.cerc.mis.page.IMenuBar;
import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.UrlRecord;
import cn.cerc.ui.menu.MenuList;
import cn.cerc.ui.parts.RightMenus;
import cn.cerc.ui.parts.UIFormVertical;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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
            out.printf("<title>%s</title>\n", R.asString(form.getHandle(), this.getForm().getName()));
        } else {
            out.printf("<title>%s</title>\n", R.asString(form.getHandle(), MenuList.create(this.getForm().getHandle()).getName(formId)));
        }

        // 所有的请求都不发送 referrer
        builder.append("<meta name=\"referrer\" content=\"no-referrer\" />");
        builder.append("<meta name=\"format-detection\" content=\"telephone=no\" />");
        builder.append("<meta name=\"format-detection\" content=\"email=no\" />");
        out.printf("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n");
        builder.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=9; IE=8; IE=7;\"/>");
        out.printf(
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1, user-scalable=0\"/>\n");
        out.print(this.getCssHtml());
        out.print(getScriptHtml());
        builder.append("<script>");
        builder.append("var Application = new TApplication();");
        out.printf("Application.device = '%s';\n", form.getClient().getDevice());
        out.printf("Application.bottom = '%s';\n", this.getFooter().getId());
        String msg = form.getParam("message", "");
        msg = msg == null ? "" : msg.replaceAll("\r\n", "<br/>");
        out.printf("Application.message = '%s';\n", msg);
        out.printf("Application.searchFormId = '%s';\n", this.searchWaitingId);
        builder.append("$(document).ready(function() {");
        builder.append("Application.init();");
        builder.append("});");
        builder.append("</script>");
        builder.append("</head>");
        outBody(builder);
        out.println("</html>");
        out.print(builder);
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
