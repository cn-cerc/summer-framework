package cn.cerc.ui.page;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import cn.cerc.core.ISession;
import cn.cerc.core.Utils;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.mis.core.AbstractForm;
import cn.cerc.mis.core.AppClient;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.IForm;
import cn.cerc.mis.language.R;
import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.UICustomComponent;
import cn.cerc.ui.core.UrlRecord;
import cn.cerc.ui.menu.MenuList;
import cn.cerc.ui.mvc.IMenuBar;
import cn.cerc.ui.mvc.StartForms;
import cn.cerc.ui.parts.RightMenus;
import cn.cerc.ui.parts.UIComponent;

/**
 * 主体子页面(公用)
 *
 * @author 张弓
 */
public class UIPagePhone extends UIPage {
    private String searchWaitingId = "";
    private UIComponent body;

    public UIPagePhone(IForm form) {
        super();
        setForm(form);
        ServerConfig config = ServerConfig.getInstance();
        initCssFile();
        this.addCssFile(config.getProperty("phone-block.css", "jui/phone/phone-block.css"));
        initJsFile();
        this.addScriptFile(config.getProperty("phone-block.js", "jui/phone/phone-block.js"));
    }

    public void addExportFile(String service, String key) {
        if (AppClient.ee.equals(this.getForm().getClient().getDevice())) {
            ExportFile item = new ExportFile(service, key);
            this.put("export", item);
        }
    }

    @Override
    protected void writeHtml(PrintWriter out) {
        HttpServletRequest request = getRequest();

        IForm form = this.getForm();
        ISession session = form.getHandle().getSession();
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

        // 系统通知消息
        Component content = this.getContent();
        if (form instanceof AbstractForm) {
            this.getHeader().initHeader();
            if (content.getId() != null) {
                request.setAttribute(content.getId(), content);
            }
            for (Component component : content.getComponents()) {
                if (component.getId() != null) {
                    request.setAttribute(component.getId(), component);
                }
            }
        }

        // 开始输出
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");

        String menuCode = StartForms.getRequestCode(this.getForm().getRequest());
        String[] params = menuCode.split("\\.");
        String formId = params[0];
        if (Utils.isNotEmpty(this.getForm().getName())) {
            out.printf("<title>%s</title>\n", R.asString(form.getHandle(), this.getForm().getName()));
        } else {
            out.printf("<title>%s</title>\n",
                    R.asString(form.getHandle(), MenuList.create(this.getForm().getHandle()).getName(formId)));
        }

        // 所有的请求都不发送 referrer
        out.println("<meta name=\"referrer\" content=\"no-referrer\" />");
        out.println("<meta name=\"format-detection\" content=\"telephone=no\" />");
        out.println("<meta name=\"format-detection\" content=\"email=no\" />");
        out.printf("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n");
        out.printf("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>\n");
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
        out.println("<body>");
        writeBody(out);
        out.println("</body>");
        out.println("</html>");
    }

    @Deprecated // 请使用：getDocument().getContext()
    public UIComponent getBody() {
        if (body == null) {
            body = new UICustomComponent();
            body.setOwner(this.getContent());
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
