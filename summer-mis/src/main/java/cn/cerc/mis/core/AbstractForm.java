package cn.cerc.mis.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;

//@Component
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AbstractForm implements IForm {
    private static final Logger log = LoggerFactory.getLogger(AbstractForm.class);
//    private static final ClassResource res = new ClassResource(AbstractForm.class, SummerMIS.ID);
//    private static final ClassConfig config = new ClassConfig(AbstractForm.class, SummerMIS.ID);

    private String id;
    private ISession session;
    protected IHandle handle;
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

        return handle.getSession().getProperty(key);
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
    public String getView(String funcCode) throws Exception {
        HttpServletResponse response = this.getResponse();
        HttpServletRequest request = this.getRequest();
        if ("excel".equals(funcCode)) {
            response.setContentType("application/vnd.ms-excel; charset=UTF-8");
            response.addHeader("Content-Disposition", "attachment; filename=excel.csv");
        } else {
            response.setContentType("text/html;charset=UTF-8");
        }

        String CLIENTVER = request.getParameter("CLIENTVER");
        if (CLIENTVER != null)
            request.getSession().setAttribute("CLIENTVER", CLIENTVER);

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
                        method = this.getClass().getMethod(funcCode + "_phone", String.class, String.class,
                                String.class);
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
                log.warn(String.format("%s pageOutput is not IView: %s", funcCode, result));
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
    public ISession getSession() {
        return session;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
    }

    @Override
    public void setHandle(IHandle handle) {
        this.handle = handle;
        if (handle != null) {
            this.setSession(handle.getSession());
        }
    }

    @Override
    public IHandle getHandle() {
        return this.handle;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
    
}
