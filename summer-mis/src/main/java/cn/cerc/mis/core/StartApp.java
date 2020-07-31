package cn.cerc.mis.core;

import cn.cerc.core.IHandle;
import cn.cerc.db.core.IAppConfig;
import cn.cerc.mis.config.ApplicationConfig;
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
        String path = url.getUrl();
        if (path.contains("?code=")) {
            log.info("url {}", url.getUrl());
        }

        String uri = req.getRequestURI();
        Application.get(req);

        // 处理默认首页问题
        if ("/".equals(uri)) {
            if (req.getParameter(ClientDevice.APP_CLIENT_ID) != null) {
                req.getSession().setAttribute(ClientDevice.APP_CLIENT_ID, req.getParameter(ClientDevice.APP_CLIENT_ID));
            }
            if (req.getParameter(ClientDevice.APP_DEVICE_TYPE) != null) {
                req.getSession().setAttribute(ClientDevice.APP_DEVICE_TYPE,
                        req.getParameter(ClientDevice.APP_DEVICE_TYPE));
            }

            IAppConfig conf = Application.getAppConfig();
            resp.sendRedirect(String.format("%s%s", ApplicationConfig.App_Path, conf.getFormWelcome()));
            return;
        } else if ("/MobileConfig".equals(uri) || "/mobileConfig".equals(uri)) {
            if (req.getParameter(ClientDevice.APP_CLIENT_ID) != null) {
                req.getSession().setAttribute(ClientDevice.APP_CLIENT_ID, req.getParameter(ClientDevice.APP_CLIENT_ID));
            }
            if (req.getParameter(ClientDevice.APP_DEVICE_TYPE) != null) {
                req.getSession().setAttribute(ClientDevice.APP_DEVICE_TYPE,
                        req.getParameter(ClientDevice.APP_DEVICE_TYPE));
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

                IHandle handle = Application.getHandle();
                handle.setProperty(Application.sessionId, req.getSession().getId());
                form.setHandle(handle);
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