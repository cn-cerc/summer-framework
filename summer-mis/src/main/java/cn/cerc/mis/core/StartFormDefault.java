package cn.cerc.mis.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.cerc.core.IHandle;

//@Controller
//@Scope(WebApplicationContext.SCOPE_REQUEST)
//@RequestMapping("/forms")
public class StartFormDefault implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(StartFormDefault.class);
    private ApplicationContext context;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Autowired
    @Qualifier("handle")
    private IHandle handle;
    @Autowired
    @Qualifier("clientDevice")
    private ClientDevice clientDevice;
    @Autowired
    private IPassport passport;
    private IAppLogin appLogin;

    @RequestMapping("/{formId}.{funcId}")
    public String execute(@PathVariable String formId, @PathVariable String funcId) {
        log.debug(String.format("formId: %s, funcId: %s", formId, funcId));
        if (!context.containsBean(formId))
            return String.format("formId: %s, funcId: %s", formId, funcId);

        Application.setContext(context);
        appLogin = Application.getBean(IAppLogin.class, "appLogin", "appLoginManage", "appLoginManageDefault");
        IForm form = context.getBean(formId, IForm.class);
        try {
            form.setHandle(handle);
            form.setRequest(request);
            form.setResponse(response);

            clientDevice.setRequest(request);

            handle.setProperty(Application.sessionId, request.getSession().getId());
            handle.setProperty(Application.deviceLanguage, clientDevice.getLanguage());

            request.setAttribute("myappHandle", handle);
            request.setAttribute("_showMenu_", !ClientDevice.device_ee.equals(clientDevice.getDevice()));

            form.setClient(clientDevice);

            if ("excel".equals(funcId)) {
                response.setContentType("application/vnd.ms-excel; charset=UTF-8");
                response.addHeader("Content-Disposition", "attachment; filename=excel.csv");
            } else
                response.setContentType("text/html;charset=UTF-8");

            // 执行自动登录
            appLogin.init(form);
            String jspFile = appLogin.checkToken(clientDevice.getSid());
            if (jspFile != null) {
                log.info("需要登录： {}", request.getRequestURL());
                return jspFile;
            }

            // 执行权限检查
            passport.setHandle(handle);
            // 是否拥有此菜单调用权限
            if (!passport.passForm(form)) {
                log.warn(String.format("无权限执行 %s", request.getRequestURL()));
                throw new RuntimeException("对不起，您没有权限执行此功能！");
            }

            IPage page = form.execute();
            if (page == null)
                return null;

            jspFile = page.execute();
            if (log.isDebugEnabled())
                log.debug(jspFile);
            return jspFile;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            if (handle != null) {
                handle.close();
            }
        }
    }

    @RequestMapping("/{formId}")
    public String execute(@PathVariable String formId) {
        return execute(formId, "execute");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
