package cn.cerc.mis.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import cn.cerc.core.IHandle;
import cn.cerc.mis.language.R;
import lombok.extern.slf4j.Slf4j;

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

    @RequestMapping("/{formId}.{funcId}")
    public ModelAndView execute(@PathVariable String formId, @PathVariable String funcId) {
        log.info(String.format("formId: %s, funcId: %s", formId, funcId));
        String viewId = build(formId, funcId);
        if (viewId == null)
            return null;
        return new ModelAndView(viewId);
    }

    private String build(String formId, String funcId) {
        if (!context.containsBean(formId)) {
            return String.format("formId: %s, funcId: %s", formId, funcId);
        }

        IHandle handle = null;
        Application.setContext(context);
        IForm form = context.getBean(formId, IForm.class);
        try {
            AppClient client = new AppClient();
            client.setRequest(request);

            // 建立数据库资源
            handle = Application.getHandle();
            handle.setProperty(Application.sessionId, request.getSession().getId());
            handle.setProperty(Application.deviceLanguage, client.getLanguage());
            request.setAttribute("myappHandle", handle);
            request.setAttribute("_showMenu_", !AppClient.ee.equals(client.getDevice()));

            form.setHandle(handle);
            form.setRequest(request);
            form.setResponse(response);
            form.setClient(client);

            if ("excel".equals(funcId)) {
                response.setContentType("application/vnd.ms-excel; charset=UTF-8");
                response.addHeader("Content-Disposition", "attachment; filename=excel.csv");
            } else {
                response.setContentType("text/html;charset=UTF-8");
            }

            IAppLogin appLogin = Application.getBean(IAppLogin.class, "appLogin", "appLoginDefault");
            // 执行自动登录
            if (appLogin == null) {
                log.error("bean[appLogin] or class[IAppLogin] not define.");
                return null;
            }

            appLogin.init(form);
            String loginPage = appLogin.checkToken(client.getToken());
            if (loginPage != null) {
                log.info("需要登录： {}", request.getRequestURL());
                return loginPage;
            }

            // 执行权限检查
            IPassport passport = Application.getPassport(handle);
            // 是否拥有此菜单调用权限
            if (!passport.passForm(form)) {
                log.warn(String.format("无权限执行 %s", request.getRequestURL()));
                throw new RuntimeException(R.asString(form.getHandle(), "对不起，您没有权限执行此功能！"));
            }

            return form.getView(funcId);
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
