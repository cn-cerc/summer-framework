package cn.cerc.mis.core;

import cn.cerc.core.IHandle;
import cn.cerc.db.core.IAppConfig;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.mis.client.IServiceProxy;
import cn.cerc.mis.client.ServiceFactory;
import cn.cerc.mis.config.AppStaticFileDefault;
import cn.cerc.mis.config.ApplicationConfig;
import cn.cerc.mis.language.R;
import cn.cerc.mis.other.BufferType;
import cn.cerc.mis.other.MemoryBuffer;
import cn.cerc.mis.page.JsonPage;
import cn.cerc.mis.page.JspPage;
import cn.cerc.mis.page.RedirectPage;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

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
         * http://127.0.0.1:8103/
         * http://127.0.0.1:8103
         * http://127.0.0.1:8103/public
         * http://127.0.0.1:8103/public/
         * http://127.0.0.1:8103/favicon.ico
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
            outputErrorPage(req, resp, new RuntimeException("无效的请求：" + req.getServletPath()));
            return;
        }

        String[] params = childCode.split("\\.");
        String formId = params[0];
        String funcCode = params.length == 1 ? "execute" : params[1];

        // TODO ???
        req.setAttribute("logon", false);

        // 验证菜单是否启停
        // TODO ???
        IFormFilter formFilter = Application.getBean(IFormFilter.class, "AppFormFilter");
        if (formFilter != null) {
            if (formFilter.doFilter(resp, formId, funcCode)) {
                return;
            }
        }

        IHandle handle = null;
        try {
            IForm form = Application.getForm(req, resp, formId);
            if (form == null) {
                outputErrorPage(req, resp, new RuntimeException("error servlet:" + req.getServletPath()));
                return;
            }

            // 设备讯息
            AppClient client = new AppClient();
            client.setRequest(req);
            req.setAttribute("_showMenu_", !AppClient.ee.equals(client.getDevice()));
            form.setClient(client);

            // 建立数据库资源
            handle = Application.getHandle();
            handle.setProperty(Application.sessionId, req.getSession().getId());
            handle.setProperty(Application.deviceLanguage, client.getLanguage());
            req.setAttribute("myappHandle", handle);
            form.setHandle(handle);

            // 进行安全检查，若未登录则显示登录对话框
            if (form.logon()) {
                callForm(form, funcCode);
            } else {
                IAppLogin page = Application.getBean(IAppLogin.class, "appLogin", "appLoginDefault");
                page.init(form);
                String cmd = page.checkToken(client.getToken());
                if (cmd != null) {
                    // 若需要登录，则跳转到登录页
                    if (cmd.startsWith("redirect:")) {
                        String redirect = cmd.substring(9);
                        redirect = resp.encodeRedirectURL(redirect);
                        resp.sendRedirect(redirect);
                    } else {
                        String url = String.format("/WEB-INF/%s/%s", Application.getAppConfig().getPathForms(),
                                cmd);
                        request.getServletContext().getRequestDispatcher(url).forward(request, response);
                    }
                } else {
                    // 已授权通过
                    callForm(form, funcCode);
                }
            }
        } catch (Exception e) {
            outputErrorPage(req, resp, e);
        } finally {
            if (handle != null) {
                handle.close();
            }
        }
    }

    private void outputErrorPage(HttpServletRequest request, HttpServletResponse response, Throwable e)
            throws ServletException, IOException {
        Throwable err = e.getCause();
        if (err == null) {
            err = e;
        }
        IAppErrorPage errorPage = Application.getBean(IAppErrorPage.class, "appErrorPage", "appErrorPageDefault");
        if (errorPage != null) {
            String result = errorPage.getErrorPage(request, response, err);
            if (result != null) {
                String url = String.format("/WEB-INF/%s/%s", Application.getAppConfig().getPathForms(), result);
                request.getServletContext().getRequestDispatcher(url).forward(request, response);
            }
        } else {
            log.warn("not define bean: errorPage");
            log.error(err.getMessage());
            err.printStackTrace();
        }
    }

    // 是否在当前设备使用此菜单，如：检验此设备是否需要设备验证码
    protected boolean passDevice(IForm form) {
        // 若是iPhone应用商店测试或地藤体验账号则跳过验证
        if (isExperienceAccount(form)) {
            return true;
        }

        String deviceId = form.getClient().getId();
        // TODO 验证码变量，需要改成静态变量，统一取值
        String verifyCode = form.getRequest().getParameter("verifyCode");
        log.debug(String.format("进行设备认证, deviceId=%s", deviceId));
        String userId = (String) form.getHandle().getProperty(Application.userId);
        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getSessionInfo, userId, deviceId)) {
            if (!buff.isNull()) {
                if (buff.getBoolean("VerifyMachine")) {
                    log.debug("已经认证过，跳过认证");
                    return true;
                }
            }

            boolean result = false;
            IServiceProxy app = ServiceFactory.get(form.getHandle());
            app.setService("SvrUserLogin.verifyMachine");
            app.getDataIn().getHead().setField("deviceId", deviceId);
            if (verifyCode != null && !"".equals(verifyCode)) {
                app.getDataIn().getHead().setField("verifyCode", verifyCode);
            }

            if (app.exec()) {
                result = true;
            } else {
                int used = app.getDataOut().getHead().getInt("Used_");
                if (used == 1) {
                    result = true;
                } else {
                    form.setParam("message", app.getMessage());
                }
            }
            if (result) {
                buff.setField("VerifyMachine", true);
            }
            return result;
        }
    }

    // 调用页面控制器指定的函数
    protected void callForm(IForm form, String funcCode) throws ServletException, IOException {
        HttpServletResponse response = form.getResponse();
        HttpServletRequest request = form.getRequest();
        if ("excel".equals(funcCode)) {
            response.setContentType("application/vnd.ms-excel; charset=UTF-8");
            response.addHeader("Content-Disposition", "attachment; filename=excel.csv");
        } else {
            response.setContentType("text/html;charset=UTF-8");
        }

        Object pageOutput;
        Method method = null;
        long startTime = System.currentTimeMillis();
        try {
            // FIXME: 2019/12/8 ??? CLIENTVER
            String CLIENTVER = request.getParameter("CLIENTVER");
            if (CLIENTVER != null) {
                request.getSession().setAttribute("CLIENTVER", CLIENTVER);
            }

            // 是否拥有此菜单调用权限
            if (!Application.getPassport(form.getHandle()).passForm(form)) {
                log.warn(String.format("无权限执行 %s", request.getRequestURL()));
                throw new RuntimeException(R.asString(form.getHandle(), "对不起，您没有权限执行此功能！"));
            }

            // 专用测试账号则跳过设备认证的判断
            if (isExperienceAccount(form)) {
                try {
                    if (form.getClient().isPhone()) {
                        try {
                            method = form.getClass().getMethod(funcCode + "_phone");
                        } catch (NoSuchMethodException e) {
                            method = form.getClass().getMethod(funcCode);
                        }
                    } else {
                        method = form.getClass().getMethod(funcCode);
                    }
                    pageOutput = method.invoke(form);
                } catch (PageException e) {
                    form.setParam("message", e.getMessage());
                    pageOutput = e.getViewFile();
                }
            } else {
                // 检验此设备是否需要设备验证码
                if (form.getHandle().getProperty(Application.userId) == null || form.passDevice() || passDevice(form)) {
                    try {
                        if (form.getClient().isPhone()) {
                            try {
                                method = form.getClass().getMethod(funcCode + "_phone");
                            } catch (NoSuchMethodException e) {
                                method = form.getClass().getMethod(funcCode);
                            }
                        } else {
                            method = form.getClass().getMethod(funcCode);
                        }
                        pageOutput = method.invoke(form);
                    } catch (PageException e) {
                        form.setParam("message", e.getMessage());
                        pageOutput = e.getViewFile();
                    }
                } else {
                    log.debug("没有进行认证过，跳转到设备认证页面");
                    ServerConfig config = ServerConfig.getInstance();
                    String supCorpNo = config.getProperty("vine.mall.supCorpNo", "");
                    // 若是专用APP登录并且是iPhone，则不跳转设备登录页，由iPhone原生客户端处理
                    if (!"".equals(supCorpNo) && form.getClient().getDevice().equals(AppClient.iphone)) {
                        try {
                            method = form.getClass().getMethod(funcCode + "_phone");
                        } catch (NoSuchMethodException e) {
                            method = form.getClass().getMethod(funcCode);
                        }
                        form.getRequest().setAttribute("needVerify", "true");
                        pageOutput = method.invoke(form);
                    } else {
                        if (form instanceof IJSONForm) {
                            JsonPage output = new JsonPage(form);
                            output.setResultMessage(false, "您的设备没有经过安全校验，无法继续作业");
                            pageOutput = output;
                        } else {
                            pageOutput = new RedirectPage(form, Application.getAppConfig().getFormVerifyDevice());
                        }
                    }
                }
            }

            // 处理返回值
            if (pageOutput != null) {
                if (pageOutput instanceof IPage) {
                    IPage output = (IPage) pageOutput;
                    String cmd = output.execute();
                    if (cmd != null) {
                        if (cmd.startsWith("redirect:")) {
                            String redirect = cmd.substring(9);
                            redirect = response.encodeRedirectURL(redirect);
                            response.sendRedirect(redirect);
                        } else {
                            String url = String.format("/WEB-INF/%s/%s", Application.getAppConfig().getPathForms(), cmd);
                            request.getServletContext().getRequestDispatcher(url).forward(request, response);
                        }
                    } else if ("GET".equals(request.getMethod())) {
                        StringBuffer jumpUrl = new StringBuffer();
                        String[] zlass = form.getClass().getName().split("\\.");
                        if (zlass.length > 0) {
                            jumpUrl.append(zlass[zlass.length - 1]);
                            jumpUrl.append(".").append(funcCode);
                        } else {
                            jumpUrl.append(request.getRequestURL().toString());
                        }
                        if (request.getParameterMap().size() > 0) {
                            jumpUrl.append("?");
                            request.getParameterMap().forEach((key, value) -> {
                                jumpUrl.append(key).append("=").append(String.join(",", value)).append("&");
                            });
                            jumpUrl.delete(jumpUrl.length() - 1, jumpUrl.length());
                        }
                        response.setHeader("jumpURL", jumpUrl.toString());
                    }
                } else {
                    log.warn(String.format("%s pageOutput is not IPage: %s", funcCode, pageOutput));
                    JspPage output = new JspPage(form);
                    output.setJspFile((String) pageOutput);
                    output.execute();
                }
            }
        } catch (Exception e) {
            outputErrorPage(request, response, e);
        } finally {
            if (method != null) {
                long timeout = 1000;
                Webpage webpage = method.getAnnotation(Webpage.class);
                if (webpage != null) {
                    timeout = webpage.timeout();
                }
                checkTimeout(form, funcCode, startTime, timeout);
            }
        }
    }

    protected void checkTimeout(IForm form, String funcCode, long startTime, long timeout) {
        long totalTime = System.currentTimeMillis() - startTime;
        if (totalTime > timeout) {
            String[] tmp = form.getClass().getName().split("\\.");
            String pageCode = tmp[tmp.length - 1] + "." + funcCode;
            String dataIn = new Gson().toJson(form.getRequest().getParameterMap());
            if (dataIn.length() > 200) {
                dataIn = dataIn.substring(0, 200);
            }
            log.warn("pageCode: {}, tickCount: {}, dataIn: {}", pageCode, totalTime, dataIn);
        }
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

    protected boolean isExperienceAccount(IForm form) {
        String userCode = form.getHandle().getUserCode();
        return LoginWhitelist.getInstance().contains(userCode);
    }

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {

    }

}
