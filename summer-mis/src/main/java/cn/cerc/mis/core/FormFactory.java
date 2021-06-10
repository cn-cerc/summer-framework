package cn.cerc.mis.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import cn.cerc.core.ClassResource;
import cn.cerc.core.ISession;
import cn.cerc.mis.SummerMIS;

@Component
public class FormFactory implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(FormFactory.class);
    // FIXME: 此处资源文件引用特殊，需要连动所有项目一起才能修改
    private static final ClassResource res = new ClassResource(Application.class, SummerMIS.ID);
    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
        Application.setContext(applicationContext);
    }

    public String getFormView(HttpServletRequest req, HttpServletResponse resp, String formId, String funcCode,
            String... pathVariables) {
        // 设置登录开关
        req.setAttribute("logon", false);

        // 建立数据库资源
        try (BasicHandle handle = new BasicHandle()) {
            req.setAttribute("myappHandle", handle);
            ISession session = handle.getSession();
            session.setProperty(Application.SessionId, req.getSession().getId());
            session.setProperty(ISession.REQUEST, req);

            IForm form = getForm(req, resp, formId);
            if (form == null) {
                outputErrorPage(req, resp, new RuntimeException("error servlet:" + req.getServletPath()));
                return null;
            }
            form.setSession(session);

            // 设备讯息
            AppClient client = new AppClient();
            client.setRequest(req);
            session.setProperty(ISession.LANGUAGE_ID, client.getLanguage());

            req.setAttribute("_showMenu_", !AppClient.ee.equals(client.getDevice()));
            form.setClient(client);

            session.setProperty(ISession.TOKEN, req.getSession().getAttribute(ISession.TOKEN));

            form.setId(formId);

            // 传递路径变量
            form.setPathVariables(pathVariables);

            // 匿名访问
            if (form.allowGuestUser()) {
                return form.getView(funcCode);
            }

            // 是否登录
            if (!session.logon()) {
                // 登录验证
                IAppLogin appLogin = Application.getBean(form, IAppLogin.class);
                String loginView = appLogin.getLoginView(form);
                if ("".equals(loginView)) {
                    return null;
                }
                if (loginView != null)
                    return loginView;
            }

            // 权限检查
            if (!Application.getPassport(form).pass(form)) {
                resp.setContentType("text/html;charset=UTF-8");
                JsonPage output = new JsonPage(form);
                output.setResultMessage(false, res.getString(1, "对不起，您没有权限执行此功能！"));
                output.execute();
                return null;
            }

            // 设备检查
            if (form.isSecurityDevice()) {
                return form.getView(funcCode);
            }

            ISecurityDeviceCheck deviceCheck = Application.getBean(form, ISecurityDeviceCheck.class);
            switch (deviceCheck.pass(form)) {
            case PASS:
                log.debug("{}.{}", formId, funcCode);
                return form.getView(funcCode);
            case CHECK:
                return "redirect:" + Application.getConfig().getVerifyDevicePage();
            default:
                resp.setContentType("text/html;charset=UTF-8");
                JsonPage output = new JsonPage(form);
                output.setResultMessage(false, res.getString(2, "对不起，当前设备被禁止使用！"));
                output.execute();
                return null;
            }
        } catch (Exception e) {
            outputErrorPage(req, resp, e);
            return null;
        }
    }

    public void outputView(HttpServletRequest request, HttpServletResponse response, String url)
            throws IOException, ServletException {
        if (url == null)
            return;

        if (url.startsWith("redirect:")) {
            String redirect = url.substring(9);
            redirect = response.encodeRedirectURL(redirect);
            response.sendRedirect(redirect);
            return;
        }

        // 输出jsp文件
        String jspFile = String.format("/WEB-INF/%s/%s", Application.getConfig().getFormsPath(), url);
        request.getServletContext().getRequestDispatcher(jspFile).forward(request, response);
    }

    public void outputErrorPage(HttpServletRequest request, HttpServletResponse response, Throwable e) {
        Throwable err = e.getCause();
        if (err == null) {
            err = e;
        }
        IAppErrorPage errorPage = Application.getBean(IAppErrorPage.class);
        if (errorPage != null) {
            String result = errorPage.getErrorPage(request, response, err);
            if (result != null) {
                String url = String.format("/WEB-INF/%s/%s", Application.getConfig().getFormsPath(), result);
                try {
                    request.getServletContext().getRequestDispatcher(url).forward(request, response);
                } catch (ServletException | IOException e1) {
                    log.error(e1.getMessage());
                    e1.printStackTrace();
                }
            }
        } else {
            log.warn("not define bean: errorPage");
            log.error(err.getMessage());
            err.printStackTrace();
        }
    }

    private IForm getForm(HttpServletRequest req, HttpServletResponse resp, String formId) {
        if (formId == null || "".equals(formId) || "service".equals(formId)) {
            return null;
        }

        String beanId = formId;
        if (!context.containsBean(formId)) {
            if (!formId.substring(0, 2).toUpperCase().equals(formId.substring(0, 2))) {
                beanId = formId.substring(0, 1).toLowerCase() + formId.substring(1);
            }
        }

        if (!context.containsBean(beanId)) {
            throw new RuntimeException(String.format("form %s not find!", beanId));
        }

        IForm form = context.getBean(beanId, IForm.class);
        if (form != null) {
            form.setRequest(req);
            form.setResponse(resp);
        }

        return form;
    }

}
