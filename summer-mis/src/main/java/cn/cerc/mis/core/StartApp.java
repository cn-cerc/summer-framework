package cn.cerc.mis.core;

import cn.cerc.core.IHandle;
import cn.cerc.db.core.IAppConfig;
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

@Slf4j
@Deprecated // 请改使用 StartAppDefault
public class StartApp implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        Application.get(req);

        // 处理默认首页问题
        if (uri.equals("/")) {
            if (req.getParameter(ClientDevice.CLIENT_ID) != null)
                req.getSession().setAttribute(ClientDevice.CLIENT_ID, req.getParameter(ClientDevice.CLIENT_ID));
            if (req.getParameter(ClientDevice.DEVICE_TYPE) != null)
                req.getSession().setAttribute(ClientDevice.DEVICE_TYPE,
                        req.getParameter(ClientDevice.DEVICE_TYPE));

            IAppConfig conf = Application.getAppConfig();
            resp.sendRedirect(String.format("/public/%s", conf.getFormWelcome()));
            return;
        } else if (uri.equals("/MobileConfig") || uri.equals("/mobileConfig")) {
            if (req.getParameter(ClientDevice.CLIENT_ID) != null)
                req.getSession().setAttribute(ClientDevice.CLIENT_ID, req.getParameter(ClientDevice.CLIENT_ID));
            if (req.getParameter(ClientDevice.DEVICE_TYPE) != null)
                req.getSession().setAttribute(ClientDevice.DEVICE_TYPE,
                        req.getParameter(ClientDevice.DEVICE_TYPE));
            try {
                IForm form;
                if (Application.get(req).containsBean("mobileConfig"))
                    form = Application.getBean("mobileConfig", IForm.class);
                else
                    form = Application.getBean("MobileConfig", IForm.class);
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
        } else {
            StringBuffer url = req.getRequestURL();
            log.info("{}", url.toString());
        }

        chain.doFilter(req, resp);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }
}