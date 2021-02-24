package cn.cerc.mis.core;

import cn.cerc.core.IHandle;
import cn.cerc.mis.language.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
@Scope(WebApplicationContext.SCOPE_REQUEST)
@RequestMapping("/springmvc")
public class StartFormDefault implements ApplicationContextAware {

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
    public ModelAndView execute(@PathVariable String formId, @PathVariable String funcId) {
        log.info(String.format("formId: %s, funcId: %s", formId, funcId));
        String jspFile = build(formId, funcId);
        if (jspFile == null)
            return null;
        return new ModelAndView(jspFile);
    }

    private String build(String formId, String funcId) {
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
//            if (appLogin == null) {
//                log.error("bean[appLogin] or class[IAppLogin] not define.");
//                return null;
//            }

            String jspFile;
//            appLogin.init(form);
//            String jspFile = appLogin.checkToken(appClient.getToken());
//            if (jspFile != null) {
//                log.info("需要登录： {}", request.getRequestURL());
//                return jspFile;
//            }

            // 执行权限检查
            passport.setHandle(handle);
            // 是否拥有此菜单调用权限
            if (!passport.passForm(form)) {
                log.warn(String.format("无权限执行 %s", request.getRequestURL()));
                throw new RuntimeException(R.asString(form.getHandle(), "对不起，您没有权限执行此功能！"));
            }

            Object pageOutput = null;
            Method method = null;
            try {
                if (form.getClient().isPhone()) {
                    try {
                        method = form.getClass().getMethod(funcId + "_phone");
                    } catch (NoSuchMethodException e) {
                        method = form.getClass().getMethod(funcId);
                    }
                } else {
                    method = form.getClass().getMethod(funcId);
                }
                pageOutput = method.invoke(form);
            } catch (PageException e) {
                form.setParam("message", e.getMessage());
                pageOutput = e.getViewFile();
            }

            if (pageOutput instanceof IPage) {
                IPage output = (IPage) pageOutput;
                output.setForm(form);
                return output.execute();
            } else if (pageOutput instanceof String) {
                return (String) pageOutput;
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            if (handle != null) {
                handle.close();
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
