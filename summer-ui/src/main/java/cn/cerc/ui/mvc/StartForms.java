package cn.cerc.ui.mvc;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import cn.cerc.db.core.IAppConfig;
import cn.cerc.mis.config.AppStaticFileDefault;
import cn.cerc.mis.config.ApplicationConfig;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.RequestData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StartForms implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        log.debug("uri {}", uri);

        /*
         * http://127.0.0.1:8103/ http://127.0.0.1:8103 http://127.0.0.1:8103/public
         * http://127.0.0.1:8103/public/ http://127.0.0.1:8103/favicon.ico
         */
        if (StringUtils.countMatches(uri, "/") < 2 && !uri.contains("favicon.ico")) {
            IAppConfig conf = Application.getAppConfig();
            String redirect = String.format("%s%s", ApplicationConfig.App_Path, conf.getFormWelcome());
            redirect = resp.encodeRedirectURL(redirect);
            resp.sendRedirect(redirect);
            return;
        }

        // 1、静态文件直接输出
        if (AppStaticFileDefault.getInstance().isStaticFile(uri)) {
            // 默认没有重定向，直接读取资源文件的默认路径
            // TODO 暂时按该方法放行（jar包的资源文件）
            if (uri.contains("imgZoom")) {
                chain.doFilter(req, resp);
                return;
            }

            /*
             * 1、 此处的 getPathForms 对应资源文件目录的forms，可自行定义成其他路径，注意配套更新 AppConfig
             * 2、截取当前的资源路径，将资源文件重定向到容器中的项目路径 3、例如/ /131001/images/systeminstall-pc.png ->
             * /forms/images/systeminstall-pc.png
             */
            log.debug("before {}", uri);
            IAppConfig conf = Application.getAppConfig();

            int index = uri.indexOf("/", 2);
            if (index < 0) {
                request.getServletContext().getRequestDispatcher(uri).forward(request, response);
            } else {
                String source = "/" + conf.getPathForms() + uri.substring(index);
                request.getServletContext().getRequestDispatcher(source).forward(request, response);
                log.debug("after  {}", source);
            }
            return;
        }
        if (uri.contains("service/")) {
            chain.doFilter(req, resp);
            return;
        }
        if (uri.contains("task/")) {
            chain.doFilter(req, resp);
            return;
        }
        if (uri.contains("docs/")) {
            chain.doFilter(req, resp);
            return;
        }

        // 2、处理Url请求
        String childCode = getRequestCode(req);
        if (childCode == null) {
            Application.outputErrorPage(req, resp, new RuntimeException("无效的请求：" + req.getServletPath()));
            return;
        }

        String[] params = childCode.split("\\.");
        String formId = params[0];
        String funcCode = params.length == 1 ? "execute" : params[1];

        Application.startForm(req, resp, formId, funcCode);
    }

    public static String getRequestCode(HttpServletRequest req) {
        String url = null;
        log.debug("servletPath {}", req.getServletPath());
        String[] args = req.getServletPath().split("/");
        if (args.length == 2 || args.length == 3) {
            if ("".equals(args[0]) && !"".equals(args[1])) {
                if (args.length == 3) {
                    url = args[2];
                } else {
                    String token = (String) req.getAttribute(RequestData.TOKEN);
                    IAppConfig conf = Application.getAppConfig();
                    if (token != null && !"".equals(token)) {
                        url = conf.getFormDefault();
                    } else {
                        url = conf.getFormWelcome();
                    }
                }
            }
        }
        return url;
    }

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {

    }

}
