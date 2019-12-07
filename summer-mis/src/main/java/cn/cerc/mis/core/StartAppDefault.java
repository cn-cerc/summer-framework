package cn.cerc.mis.core;

import cn.cerc.core.IHandle;
import cn.cerc.db.core.IAppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@Controller
//@Scope(WebApplicationContext.SCOPE_REQUEST)
//@RequestMapping("/")
public class StartAppDefault {
    @Autowired
    private HttpServletRequest req;
    @Autowired
    private HttpServletResponse resp;
    @Autowired
    @Qualifier("appConfig")
    private IAppConfig appConfig;

    @RequestMapping("/")
    public String doGet() {
        if (req.getParameter(ClientDevice.client_id) != null)
            req.getSession().setAttribute(ClientDevice.client_id, req.getParameter(ClientDevice.client_id));
        if (req.getParameter(ClientDevice.device) != null)
            req.getSession().setAttribute(ClientDevice.device, req.getParameter(ClientDevice.device));

        String url = String.format("redirect:/%s/%s", appConfig.getPathForms(), appConfig.getFormWelcome());
        return url;
    }

    @RequestMapping("/MobileConfig")
    @Deprecated
    public String MobileConfig() {
        return mobileConfig();
    }

    @RequestMapping("/mobileConfig")
    public String mobileConfig() {
        if (req.getParameter(ClientDevice.client_id) != null)
            req.getSession().setAttribute(ClientDevice.client_id, req.getParameter(ClientDevice.client_id));
        if (req.getParameter(ClientDevice.device) != null)
            req.getSession().setAttribute(ClientDevice.device, req.getParameter(ClientDevice.device));
        try {
            IForm form = Application.getBean(IForm.class, "MobileConfig", "mobileConfig");
            form.setRequest(req);
            form.setResponse(resp);

            IHandle handle = Application.getHandle();
            try {
                handle.setProperty(Application.sessionId, req.getSession().getId());
                form.setHandle(handle);
                IPage page = form.execute();
                return page.execute();
            } finally {
                handle.close();
            }
        } catch (Exception e) {
            try {
                resp.getWriter().print(e.getMessage());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }
    }
}
