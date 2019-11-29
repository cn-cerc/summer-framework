package cn.cerc.mis.core;

import java.io.IOException;
import java.lang.reflect.Method;

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

import com.google.gson.Gson;

import cn.cerc.core.IHandle;
import cn.cerc.db.core.IAppConfig;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.mis.config.IAppStaticFile;
import cn.cerc.mis.other.BufferType;
import cn.cerc.mis.other.MemoryBuffer;
import cn.cerc.mis.page.JspPage;
import cn.cerc.mis.page.RedirectPage;

public class StartForms implements Filter {

    private static final Logger log = LoggerFactory.getLogger(StartForms.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String uri = req.getRequestURI();

        // 遇到静态文件直接输出
        IAppStaticFile staticFile = Application.getBean(IAppStaticFile.class, "appStaticFile", "appStaticFileDefault");
        if (staticFile.isStaticFile(uri)) {
            // 默认没有重定向，直接读取资源文件的默认路径
            // chain.doFilter(req, resp);

            /*
             * 1、 此处的 getPathForms 对应资源文件目录的forms，可自行定义成其他路径，注意配套更新 AppConfig
             * 2、截取当前的资源路径，将资源文件重定向到容器中的项目路径 3、例如/ /131001/images/systeminstall-pc.png ->
             * /forms/images/systeminstall-pc.png
             */
            log.info("before {}", uri);
            IAppConfig conf = Application.getAppConfig();
            String source = "/" + conf.getPathForms() + uri.substring(uri.indexOf("/", 2));
            request.getServletContext().getRequestDispatcher(source).forward(request, response);
            log.info("after  {}", source);
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

        String childCode = getRequestCode(req);
        if (childCode == null) {
            outputErrorPage(req, resp, new RuntimeException("无效的请求：" + childCode));
            return;
        }

        String[] params = childCode.split("\\.");
        String formId = params[0];
        String funcCode = params.length == 1 ? "execute" : params[1];

        req.setAttribute("logon", false);

        // 验证菜单是否启停
        IFormFilter formFilter = Application.getBean(IFormFilter.class, "AppFormFilter");
        if (formFilter != null) {
            if (formFilter.doFilter(resp, formId, funcCode)) {
                return;
            }
        }

        IForm form = null;
        try {
            form = Application.getForm(req, resp, formId);
            if (form == null) {
                outputErrorPage(req, resp, new RuntimeException("error servlet:" + req.getServletPath()));
                return;
            }

            // 设备讯息
            ClientDevice info = new ClientDevice();
            info.setRequest(req);
            req.setAttribute("_showMenu_", !ClientDevice.device_ee.equals(info.getDevice()));
            form.setClient(info);

            // 建立数据库资源
            IHandle handle = Application.getHandle();
            try {
                handle.setProperty(Application.sessionId, req.getSession().getId());
                handle.setProperty(Application.deviceLanguage, info.getLanguage());
                req.setAttribute("myappHandle", handle);
                form.setHandle(handle);

                log.debug("进行安全检查，若未登录则显示登录对话框");

                if (!form.logon()) {
                    IAppLogin page = Application.getBean(IAppLogin.class, "appLogin", "appLoginManage",
                            "appLoginDefault");
                    page.init(form);
                    String cmd = page.checkToken(info.getSid());
                    if (cmd != null) {
                        // 若需要登录，则跳转到登录页
                        if (cmd.startsWith("redirect:")) {
                            resp.sendRedirect(cmd.substring(9));
                        } else {
                            String url = String.format("/WEB-INF/%s/%s", Application.getAppConfig().getPathForms(),
                                    cmd);
                            request.getServletContext().getRequestDispatcher(url).forward(request, response);
                        }
                    } else // 已授权通过
                        callForm(form, funcCode);
                } else {
                    callForm(form, funcCode);
                }
            } catch (Exception e) {
                outputErrorPage(req, resp, e);
            } finally {
                if (handle != null) {
                    handle.close();
                }
            }
        } catch (Exception e) {
            outputErrorPage(req, resp, e);
        }
    }

    // 取得页面默认设置，如出错时指向哪个页面
    protected IAppConfig createConfig() {
        return Application.getAppConfig();
    }

    protected boolean checkEnableTime() {
        // Calendar cal = Calendar.getInstance();
        // 月底最后一天
        // if (TDate.Today().compareTo(TDate.Today().monthEof()) == 0) {
        // if (cal.get(Calendar.HOUR_OF_DAY) >= 23)
        // throw new
        // RuntimeException("系统现正在进行月初例行维护，维护时间为月底晚上23点至月初早上5点，请您在这段时间内不要使用系统，谢谢！");
        // }
        // 月初第一天
        // if (TDate.Today().compareTo(TDate.Today().monthBof()) == 0)
        // if (cal.get(Calendar.HOUR_OF_DAY) < 5)
        // throw new
        // RuntimeException("系统现正在进行月初例行维护，维护时间为月底晚上23点至月初早上5点，请您在这段时间内不要使用系统，谢谢！");
        return true;
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
            LocalService app = new LocalService(form.getHandle());
            app.setService("SvrUserLogin.verifyMachine");
            app.getDataIn().getHead().setField("deviceId", deviceId);
            if (verifyCode != null && !"".equals(verifyCode))
                app.getDataIn().getHead().setField("verifyCode", verifyCode);

            if (app.exec())
                result = true;
            else {
                int used = app.getDataOut().getHead().getInt("Used_");
                if (used == 1)
                    result = true;
                else
                    form.setParam("message", app.getMessage());
            }
            if (result)
                buff.setField("VerifyMachine", true);
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
        } else
            response.setContentType("text/html;charset=UTF-8");

        Object pageOutput = "";
        String sid = request.getParameter(RequestData.appSession_Key);
        if (sid == null || sid.equals(""))
            sid = request.getSession().getId();

        Method method = null;
        long startTime = System.currentTimeMillis();
        try {
            String CLIENTVER = request.getParameter("CLIENTVER");
            if (CLIENTVER != null)
                request.getSession().setAttribute("CLIENTVER", CLIENTVER);

            // 是否拥有此菜单调用权限
            if (!Application.getPassport(form.getHandle()).passForm(form)) {
                log.warn(String.format("无权限执行 %s", request.getRequestURL()));
                throw new RuntimeException("对不起，您没有权限执行此功能！");
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
                    } else
                        method = form.getClass().getMethod(funcCode);
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
                    ServerConfig config = new ServerConfig();
                    String supCorpNo = config.getProperty("vine.mall.supCorpNo", "");
                    // 若是专用APP登陆并且是iPhone，则不跳转设备登陆页，由iPhone原生客户端处理
                    if (!"".equals(supCorpNo) && form.getClient().getDevice().equals(ClientDevice.device_iphone)) {
                        try {
                            method = form.getClass().getMethod(funcCode + "_phone");
                        } catch (NoSuchMethodException e) {
                            method = form.getClass().getMethod(funcCode);
                        }
                        form.getRequest().setAttribute("needVerify", "true");
                        pageOutput = method.invoke(form);
                    } else {
                        pageOutput = new RedirectPage(form, Application.getAppConfig().getFormVerifyDevice());
                    }
                }
            }

            // 处理返回值
            if (pageOutput != null) {
                if (pageOutput instanceof IPage) {
                    IPage output = (IPage) pageOutput;
                    String cmd = output.execute();
                    if (cmd != null) {
                        if (cmd.startsWith("redirect:"))
                            response.sendRedirect(cmd.substring(9));
                        else {
                            String url = String.format("/WEB-INF/%s/%s", Application.getAppConfig().getPathForms(),
                                    cmd);
                            request.getServletContext().getRequestDispatcher(url).forward(request, response);
                        }
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
                if (webpage != null)
                    timeout = webpage.timeout();
                checkTimeout(form, funcCode, startTime, timeout);
            }
        }
    }

    protected void checkTimeout(IForm form, String funcCode, long startTime, long timeout) {
        long totalTime = System.currentTimeMillis() - startTime;
        if (totalTime > timeout) {
            String tmp[] = form.getClass().getName().split("\\.");
            String pageCode = tmp[tmp.length - 1] + "." + funcCode;
            String dataIn = new Gson().toJson(form.getRequest().getParameterMap());
            if (dataIn.length() > 200)
                dataIn = dataIn.substring(0, 200);
            log.warn(String.format("pageCode:%s, tickCount:%s, dataIn: %s", pageCode, totalTime, dataIn));
        }
    }

    protected String getRequestCode(HttpServletRequest req) {
        String url = null;
        String args[] = req.getServletPath().split("/");
        if (args.length == 2 || args.length == 3) {
            if (args[0].equals("") && !args[1].equals("")) {
                if (args.length == 3)
                    url = args[2];
                else {
                    String sid = (String) req.getAttribute(RequestData.appSession_Key);
                    IAppConfig conf = Application.getAppConfig();
                    if (sid != null && !"".equals(sid))
                        url = conf.getFormDefault();
                    else
                        url = conf.getFormWelcome();
                }
            }
        }
        return url;
    }

    protected boolean isExperienceAccount(IForm form) {
        return getIphoneAppstoreAccount().equals(form.getHandle().getUserCode())
                || getBaseVerAccount().equals(form.getHandle().getUserCode())
                || getLineWinderAccount().equals(form.getHandle().getUserCode())
                || getTaiWanAccount().equals(form.getHandle().getUserCode());
    }

    // iPhone 上架时专用测试帐号以及专业版体验账号
    protected String getIphoneAppstoreAccount() {
        return "15202406";
    }

    // 基础版体验账号
    protected String getBaseVerAccount() {
        return "16307405";
    }

    // 喜曼多专用APP测试账号与iPhone上架测试账号
    protected String getSimagoAccount() {
        return "47583201";
    }

    // 狼王专用APP测试账号与iPhone上架测试账号
    protected String getLineWinderAccount() {
        return "15531101";
    }

    // 台湾地区地藤普及版测试账号
    protected String getTaiWanAccount() {
        return "47598601";
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    private static void outputErrorPage(HttpServletRequest request, HttpServletResponse response, Throwable e)
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
}
