package cn.cerc.mis.core;

import cn.cerc.core.ClassResource;
import cn.cerc.core.IHandle;
import cn.cerc.mis.language.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
//@Controller
//@Scope(WebApplicationContext.SCOPE_REQUEST)
//@RequestMapping("/forms")
public class StartFormDefault implements ApplicationContextAware {
    private static final ClassResource res = new ClassResource("summer-mvc", StartFormDefault.class);

    private ApplicationContext context;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    @Qualifier("handle")
    private IHandle handle;

    @Autowired
    @Qualifier("appClient")
    private AppClient appClient;

    @Autowired
    private IPassport passport;

    private IAppLogin appLogin;

    @RequestMapping("/{formId}.{funcId}")
    public String execute(@PathVariable String formId, @PathVariable String funcId) {
        log.debug(String.format("formId: %s, funcId: %s", formId, funcId));
        if (!context.containsBean(formId)) {
            return String.format("formId: %s, funcId: %s", formId, funcId);
        }

        Application.setContext(context);
        appLogin = Application.getBean(IAppLogin.class, "appLogin");
        IForm form = context.getBean(formId, IForm.class);
        try {
            form.setHandle(handle);
            form.setRequest(request);
            form.setResponse(response);

            appClient.setRequest(request);

            handle.setProperty(Application.sessionId, request.getSession().getId());
            handle.setProperty(Application.deviceLanguage, appClient.getLanguage());

            request.setAttribute("myappHandle", handle);
            request.setAttribute("_showMenu_", !AppClient.ee.equals(appClient.getDevice()));

            form.setClient(appClient);

            if ("excel".equals(funcId)) {
                response.setContentType("application/vnd.ms-excel; charset=UTF-8");
                response.addHeader("Content-Disposition", "attachment; filename=excel.csv");
            } else {
                response.setContentType("text/html;charset=UTF-8");
            }

            // 执行自动登录
            appLogin.init(form);
            String jspFile = appLogin.checkToken(appClient.getToken());
            if (jspFile != null) {
                log.info("url need login ： {}", request.getRequestURL());
                return jspFile;
            }

            // 执行权限检查
            passport.setHandle(handle);
            // 是否拥有此菜单调用权限
            if (!passport.passForm(form)) {
                log.warn(String.format("no permission to execute %s", request.getRequestURL()));
                throw new RuntimeException(R.asString(form.getHandle(), res.getString(1, "对不起，您没有权限执行此功能！")));
            }

            IPage page = form.execute();
            if (page == null) {
                return null;
            }

            jspFile = page.execute();
            if (log.isDebugEnabled()) {
                log.debug(jspFile);
            }
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
