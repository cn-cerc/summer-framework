package cn.cerc.mis.core;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.ISession;
import cn.cerc.db.core.IAppConfig;
import cn.cerc.mvc.SummerMVC;

//@Controller
//@Scope(WebApplicationContext.SCOPE_REQUEST)
//@RequestMapping("/")
@Deprecated
//TODO StartAppDefault 此对象不应该存在框架中
public class StartAppDefault {
    private static final ClassConfig config = new ClassConfig(StartAppDefault.class, SummerMVC.ID);
    @Autowired
    private HttpServletRequest req;
    @Autowired
    private HttpServletResponse resp;
    @Autowired
    @Qualifier("appConfig")
    private IAppConfig appConfig;

    @RequestMapping("/")
    public String doGet() {
        if (req.getParameter(AppClient.CLIENT_ID) != null) {
            req.getSession().setAttribute(AppClient.CLIENT_ID, req.getParameter(AppClient.CLIENT_ID));
        }
        if (req.getParameter(AppClient.DEVICE) != null) {
            req.getSession().setAttribute(AppClient.DEVICE, req.getParameter(AppClient.DEVICE));
        }

        return String.format("redirect:/%s/%s", config.getString(Application.PATH_FORMS, "forms"),
                config.getString(Application.FORM_WELCOME, "welcome"));
    }

    @RequestMapping("/MobileConfig")
    @Deprecated
    public String MobileConfig() {
        return mobileConfig();
    }

    @RequestMapping("/mobileConfig")
    public String mobileConfig() {
        if (req.getParameter(AppClient.CLIENT_ID) != null) {
            req.getSession().setAttribute(AppClient.CLIENT_ID, req.getParameter(AppClient.CLIENT_ID));
        }
        if (req.getParameter(AppClient.DEVICE) != null) {
            req.getSession().setAttribute(AppClient.DEVICE, req.getParameter(AppClient.DEVICE));
        }
        try {
            IForm form = Application.getBean(IForm.class, "MobileConfig", "mobileConfig");
            form.setRequest(req);
            form.setResponse(resp);

            ISession session = Application.createSession();
            try {
                session.setProperty(Application.sessionId, req.getSession().getId());
                form.setHandle(new Handle(session));
                IPage page = form.execute();
                return page.execute();
            } finally {
                session.close();
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
