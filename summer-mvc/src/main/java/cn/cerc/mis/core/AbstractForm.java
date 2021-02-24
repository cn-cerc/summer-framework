package cn.cerc.mis.core;

import cn.cerc.core.IHandle;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//@Component
//@Scope(WebApplicationContext.SCOPE_REQUEST)
public abstract class AbstractForm extends AbstractHandle implements IForm {
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
    public String getView(String funcId) {
        return null;
    }

    @Override
    public void outView(String funcCode, String url) throws IOException, ServletException {
        if (url == null)
            return;

        if (url.startsWith("redirect:")) {
            String redirect = url.substring(9);
            redirect = response.encodeRedirectURL(redirect);
            response.sendRedirect(redirect);
            return;
        }

        if ("GET".equals(request.getMethod())) {
            StringBuffer jumpUrl = new StringBuffer();
            String[] zlass = this.getClass().getName().split("\\.");
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
        
        String jspFile = String.format("/WEB-INF/%s/%s", Application.getAppConfig().getPathForms(), url);
        request.getServletContext().getRequestDispatcher(jspFile).forward(request, response);
    }
    
    
}
