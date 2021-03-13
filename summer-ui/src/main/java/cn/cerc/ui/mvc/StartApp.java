package cn.cerc.ui.mvc;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.IAppConfig;
import cn.cerc.mis.config.ApplicationConfig;
import cn.cerc.mis.core.AppClient;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.DefaultHandle;
import cn.cerc.mis.core.IForm;
import cn.cerc.mis.core.IPage;
import cn.cerc.mvc.SummerMVC;
import cn.cerc.ui.core.UrlRecord;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Deprecated // 请改使用 StartAppDefault
public class StartApp implements Filter {
    private static final ClassConfig config = new ClassConfig(StartApp.class, SummerMVC.ID);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        StringBuffer builder = req.getRequestURL();
        UrlRecord url = new UrlRecord();
        url.setSite(builder.toString());
        Map<String, String[]> items = req.getParameterMap();
        for (String key : items.keySet()) {
            String[] values = items.get(key);
            for (String value : values) {
                url.putParam(key, value);
            }
        }
        if (url.getUrl().contains("sid")) {
            log.info("url {}", url.getUrl());
        }

        String uri = req.getRequestURI();
        Application.get(req);

        // 处理默认首页问题
        if ("/".equals(uri)) {
            if (req.getParameter(AppClient.CLIENT_ID) != null) {
                req.getSession().setAttribute(AppClient.CLIENT_ID, req.getParameter(AppClient.CLIENT_ID));
            }
            if (req.getParameter(AppClient.DEVICE) != null) {
                req.getSession().setAttribute(AppClient.DEVICE, req.getParameter(AppClient.DEVICE));
            }

            String redirect = String.format("/%s/%s", config.getString(Application.PATH_FORMS, "forms"),
                    config.getString(Application.FORM_WELCOME, "welcome"));
            redirect = resp.encodeRedirectURL(redirect);
            resp.sendRedirect(redirect);
            return;
        } else if ("/MobileConfig".equals(uri) || "/mobileConfig".equals(uri)) {
            if (req.getParameter(AppClient.CLIENT_ID) != null) {
                req.getSession().setAttribute(AppClient.CLIENT_ID, req.getParameter(AppClient.CLIENT_ID));
            }
            if (req.getParameter(AppClient.DEVICE) != null) {
                req.getSession().setAttribute(AppClient.DEVICE, req.getParameter(AppClient.DEVICE));
            }
            try {
                IForm form;
                if (Application.get(req).containsBean("mobileConfig")) {
                    form = Application.getBean("mobileConfig", IForm.class);
                } else {
                    form = Application.getBean("MobileConfig", IForm.class);
                }
                form.setRequest((HttpServletRequest) request);
                form.setResponse((HttpServletResponse) response);

                ISession session = Application.getSession();
                session.setProperty(Application.sessionId, req.getSession().getId());
                form.setHandle(new DefaultHandle(session));
                IPage page = form.execute();
                page.execute();
            } catch (Exception e) {
                resp.getWriter().print(e.getMessage());
            }
            return;
        }
        chain.doFilter(req, resp);
    }

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {

    }

}