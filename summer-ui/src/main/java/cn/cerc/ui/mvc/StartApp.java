package cn.cerc.ui.mvc;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.cerc.core.ISession;
import cn.cerc.mis.core.AppClient;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.IMobileConfig;
import cn.cerc.mis.core.IPage;
import cn.cerc.ui.core.UrlRecord;

public class StartApp implements Filter {
    private static final Logger log = LoggerFactory.getLogger(StartApp.class);

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
            log.debug("url {}", url.getUrl());
        }

        String uri = req.getRequestURI();
        Application.setContext(WebApplicationContextUtils.getRequiredWebApplicationContext(req.getServletContext()));

        // 处理默认首页问题
        if ("/".equals(uri)) {
            if (req.getParameter(AppClient.CLIENT_ID) != null) {
                req.getSession().setAttribute(AppClient.CLIENT_ID, req.getParameter(AppClient.CLIENT_ID));
            }
            if (req.getParameter(AppClient.DEVICE) != null) {
                req.getSession().setAttribute(AppClient.DEVICE, req.getParameter(AppClient.DEVICE));
            }

            String redirect = String.format("/%s/%s", Application.getConfig().getFormsPath(),
                    Application.getConfig().getWelcomePage());
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
                ISession session = Application.getSession();
                session.setProperty(Application.SessionId, req.getSession().getId());

                IMobileConfig form = Application.getBean(IMobileConfig.class);
                form.setRequest((HttpServletRequest) request);
                form.setResponse((HttpServletResponse) response);
                form.setSession(session);
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
        log.info("{} init.", this.getClass().getName());
    }

    @Override
    public void destroy() {
        log.info("{} destroy.", this.getClass().getName());
    }

}