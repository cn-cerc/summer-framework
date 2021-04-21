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

import cn.cerc.core.ClassConfig;
import cn.cerc.core.ClassResource;
import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.ITokenManage;
import cn.cerc.mis.SummerMIS;

@Component
public class FormFactory implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(FormFactory.class);
    private static final ClassConfig config = new ClassConfig(FormFactory.class, SummerMIS.ID);
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

        // 验证菜单是否启停
        IFormFilter formFilter = Application.getBean(IFormFilter.class, "AppFormFilter");
        if (formFilter != null) {
            try {
                if (formFilter.doFilter(resp, formId, funcCode)) {
                    return null;
                }
            } catch (IOException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }

        ISession session = null;
        try {
            IForm form = getForm(req, resp, formId);
            if (form == null) {
                outputErrorPage(req, resp, new RuntimeException("error servlet:" + req.getServletPath()));
                return null;
            }

            // 设备讯息
            AppClient client = new AppClient();
            client.setRequest(req);
            req.setAttribute("_showMenu_", !AppClient.ee.equals(client.getDevice()));
            form.setClient(client);

            // 建立数据库资源
            session = Application.createSession();
            ITokenManage manage = Application.getBeanDefault(ITokenManage.class, session);
            manage.resumeToken((String) req.getSession().getAttribute(RequestData.TOKEN));
            session.setProperty(Application.sessionId, req.getSession().getId());
            session.setProperty(ISession.LANGUAGE_ID, client.getLanguage());
            session.setProperty(Application.TOKEN, req.getSession().getAttribute(RequestData.TOKEN));
            session.setProperty(ISession.REQUEST, req);
            IHandle handle = new Handle(session);
            req.setAttribute("myappHandle", handle);
            form.setId(formId);
            form.setHandle(handle);

            // 传递路径变量
            form.setPathVariables(pathVariables);

            // 当前Form需要安全检查
            if (form.allowGuestUser()) {
                return form.getView(funcCode);
            }

            // 用户已登录系统
            if (session.logon()) {
                // 权限检查
                if (!Application.getPassport(session).pass(form)) {
                    resp.setContentType("text/html;charset=UTF-8");
                    JsonPage output = new JsonPage(form);
                    output.setResultMessage(false, res.getString(1, "对不起，您没有权限执行此功能！"));
                    output.execute();
                    return null;
                }
            } else {
                // 登录验证
                IAppLogin appLogin = Application.getBeanDefault(IAppLogin.class, session);
                if (!appLogin.pass(form)) {
                    return appLogin.getJspFile();
                }
            }

            // 设备校验
            if (form.isSecurityDevice()) {
                return form.getView(funcCode);
            }

            ISecurityDeviceCheck deviceCheck = Application.getBeanDefault(ISecurityDeviceCheck.class, session);
            switch (deviceCheck.pass(form)) {
            case PASS:
                log.debug("{}.{}", formId, funcCode);
                return form.getView(funcCode);
            case CHECK:
                return "redirect:" + config.getString(Application.FORM_VERIFY_DEVICE, "VerifyDevice");
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
        } finally {
            if (session != null) {
                session.close();
            }
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
        String jspFile = String.format("/WEB-INF/%s/%s", config.getString(Application.PATH_FORMS, "forms"), url);
        request.getServletContext().getRequestDispatcher(jspFile).forward(request, response);
    }

    public void outputErrorPage(HttpServletRequest request, HttpServletResponse response, Throwable e) {
        Throwable err = e.getCause();
        if (err == null) {
            err = e;
        }
        IAppErrorPage errorPage = Application.getBeanDefault(IAppErrorPage.class, null);
        if (errorPage != null) {
            String result = errorPage.getErrorPage(request, response, err);
            if (result != null) {
                String url = String.format("/WEB-INF/%s/%s", config.getString(Application.PATH_FORMS, "forms"), result);
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
