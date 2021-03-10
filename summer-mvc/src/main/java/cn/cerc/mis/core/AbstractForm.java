package cn.cerc.mis.core;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.ClassResource;
import cn.cerc.core.IHandle;
import cn.cerc.mis.client.IServiceProxy;
import cn.cerc.mis.client.ServiceFactory;
import cn.cerc.mis.other.BufferType;
import cn.cerc.mis.other.MemoryBuffer;
import cn.cerc.mvc.SummerMVC;
import lombok.extern.slf4j.Slf4j;

//@Component
//@Scope(WebApplicationContext.SCOPE_REQUEST)
@Slf4j
public class AbstractForm extends AbstractHandle implements IForm {
    private static final ClassResource res = new ClassResource(AbstractForm.class, SummerMVC.ID);
    private static final ClassConfig config = new ClassConfig(AbstractForm.class, SummerMVC.ID);

    @Autowired
    public ISystemTable systemTable;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private IClient client;
    private Map<String, String> params = new HashMap<>();
    private String name;
    private String parent;
    private String permission;
    private String module;
    private String[] pathVariables;

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public void init(AbstractForm owner) {
        this.setHandle(owner.getHandle());
        this.setClient(owner.getClient());
        this.setRequest(owner.getRequest());
        this.setResponse(owner.getResponse());
    }

    @Override
    public boolean logon() {
        if (getHandle() == null) {
            return false;
        }
        IHandle sess = (IHandle) getHandle().getProperty(null);
        return sess.logon();
    }

    @Override
    public HttpServletRequest getRequest() {
        return this.request;
    }

    @Override
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public HttpServletResponse getResponse() {
        return response;
    }

    @Override
    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public Object getProperty(String key) {
        if ("request".equals(key)) {
            return this.getRequest();
        }
        if ("session".equals(key)) {
            return this.getRequest().getSession();
        }

        return handle.getProperty(key);
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public IClient getClient() {
        if (client == null) {
            client = new AppClient();
            client.setRequest(request);
        }
        return client;
    }

    @Override
    public void setClient(IClient client) {
        this.client = client;
    }

    @Override
    public void setParam(String key, String value) {
        params.put(key, value);
    }

    @Override
    public String getParam(String key, String def) {
        return params.getOrDefault(key, def);
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    // 执行指定函数，并返回jsp文件名，若自行处理输出则直接返回null
    @Override
    public String getView(String funcCode) {
        HttpServletResponse response = this.getResponse();
        HttpServletRequest request = this.getRequest();
        if ("excel".equals(funcCode)) {
            response.setContentType("application/vnd.ms-excel; charset=UTF-8");
            response.addHeader("Content-Disposition", "attachment; filename=excel.csv");
        } else {
            response.setContentType("text/html;charset=UTF-8");
        }

        try {
            String CLIENTVER = request.getParameter("CLIENTVER");
            if (CLIENTVER != null)
                request.getSession().setAttribute("CLIENTVER", CLIENTVER);

            // 是否拥有此菜单调用权限
            if (!Application.getPassport(this.getHandle()).passForm(this)) {
                log.warn(String.format("无权限执行 %s", request.getRequestURL()));
                JsonPage output = new JsonPage(this);
                output.setResultMessage(false, res.getString(1, "对不起，您没有权限执行此功能！"));
                output.execute();
                return null;
            }

            // 专用测试账号则跳过设备认证的判断
            if (isExperienceAccount(this)) {
                return this.getPage(funcCode);
            }

            // 通过设备验证
            if (this.getHandle().getProperty(Application.userId) == null || this.passDevice() || passDevice(this)) {
                return this.getPage(funcCode);
            }

            log.debug("没有进行认证过，跳转到设备认证页面");
            // 若是专用APP登录并且是iPhone，则不跳转设备登录页，由iPhone原生客户端处理
            String supCorpNo = config.getString("vine.mall.supCorpNo", "");
            if (!"".equals(supCorpNo) && this.getClient().getDevice().equals(AppClient.iphone)) {
                this.getRequest().setAttribute("needVerify", "true");
                return this.getPage(funcCode);
            }

            if (this instanceof IJSONForm) {
                JsonPage output = new JsonPage(this);
                output.setResultMessage(false, res.getString(2, "您的设备没有经过安全校验，无法继续作业"));
                output.execute();
                return null;
            }

            // 跳转到校验设备画面
            return "redirect:" + config.getString(Application.FORM_VERIFY_DEVICE, "VerifyDevice");
        } catch (Exception e) {
            Throwable err = e.getCause();
            if (err == null) {
                err = e;
            }
            IAppErrorPage errorPage = Application.getBean(IAppErrorPage.class, "appErrorPage", "appErrorPageDefault");
            if (errorPage == null) {
                log.warn("not define bean: errorPage");
                log.error(err.getMessage());
                err.printStackTrace();
                return null;
            }
            return errorPage.getErrorPage(request, response, err);
        }
    }

    private String getPage(String funcCode) throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, ServletException, IOException {
        Object result;
        Method method = null;
        long startTime = System.currentTimeMillis();
        try {
            // 支持路径参数调用，最多3个字符串参数
            switch (this.pathVariables.length) {
            case 1: {
                if (this.getClient().isPhone()) {
                    try {
                        method = this.getClass().getMethod(funcCode + "_phone", String.class);
                    } catch (NoSuchMethodException e) {
                        method = this.getClass().getMethod(funcCode, String.class);
                    }
                } else {
                    method = this.getClass().getMethod(funcCode, String.class);
                }
                result = method.invoke(this, this.pathVariables[0]);
                break;
            }
            case 2: {
                if (this.getClient().isPhone()) {
                    try {
                        method = this.getClass().getMethod(funcCode + "_phone", String.class, String.class);
                    } catch (NoSuchMethodException e) {
                        method = this.getClass().getMethod(funcCode, String.class, String.class);
                    }
                } else {
                    method = this.getClass().getMethod(funcCode, String.class, String.class);
                }
                result = method.invoke(this, this.pathVariables[0], this.pathVariables[1]);
                break;
            }
            case 3: {
                if (this.getClient().isPhone()) {
                    try {
                        method = this.getClass().getMethod(funcCode + "_phone", String.class, String.class, String.class);
                    } catch (NoSuchMethodException e) {
                        method = this.getClass().getMethod(funcCode, String.class, String.class, String.class);
                    }
                } else {
                    method = this.getClass().getMethod(funcCode, String.class, String.class, String.class);
                }
                result = method.invoke(this, this.pathVariables[0], this.pathVariables[1], this.pathVariables[2]);
                break;
            }
            default: {
                if (this.getClient().isPhone()) {
                    try {
                        method = this.getClass().getMethod(funcCode + "_phone");
                    } catch (NoSuchMethodException e) {
                        method = this.getClass().getMethod(funcCode);
                    }
                } else {
                    method = this.getClass().getMethod(funcCode);
                }
                result = method.invoke(this);
            }
            }

            if (result == null)
                return null;

            if (result instanceof IPage) {
                IPage output = (IPage) result;
                return output.execute();
            } else {
                log.warn(String.format("%s pageOutput is not IPage: %s", funcCode, result));
                return (String) result;
            }
        } catch (PageException e) {
            this.setParam("message", e.getMessage());
            return e.getViewFile();
        } finally {
            if (method != null) {
                long timeout = 1000;
                Webpage webpage = method.getAnnotation(Webpage.class);
                if (webpage != null) {
                    timeout = webpage.timeout();
                }
                checkTimeout(this, funcCode, startTime, timeout);
            }
        }
    }

    private boolean isExperienceAccount(IForm form) {
        String userCode = form.getHandle().getUserCode();
        return LoginWhitelist.getInstance().contains(userCode);
    }

    // 是否在当前设备使用此菜单，如：检验此设备是否需要设备验证码
    private boolean passDevice(IForm form) {
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

    private void checkTimeout(IForm form, String funcCode, long startTime, long timeout) {
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

    @Override
    public void setPathVariables(String[] pathVariables) {
        this.pathVariables = pathVariables;
    }

    public String[] getPathVariables() {
        return this.pathVariables;
    }
    
    @Override
    public IPage execute() throws Exception {
        JsonPage page = new JsonPage(this);
        page.put("class", this.getClass().getName());
        page.setResultMessage(false, "page is not defined.");
        return page;
    }
}
