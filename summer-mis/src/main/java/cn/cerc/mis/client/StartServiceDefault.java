package cn.cerc.mis.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.cerc.core.DataSet;
import cn.cerc.core.IHandle;
import cn.cerc.core.Record;
import cn.cerc.core.Utils;
import cn.cerc.db.core.IAppConfig;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.IRestful;
import cn.cerc.mis.core.IService;
import cn.cerc.mis.core.IStatus;

//@Controller
//@Scope(WebApplicationContext.SCOPE_REQUEST)
//@RequestMapping("/services")
public class StartServiceDefault {
    @Autowired
    private HttpServletRequest req;
    @Autowired
    private HttpServletResponse resp;

    private static final Logger log = LoggerFactory.getLogger(StartServiceDefault.class);
    public final String outMsg = "{\"result\":%s,\"message\":\"%s\"}";
    private static Map<String, String> services;
    private static final String sessionId = "sessionId";

    @RequestMapping("/")
    @ResponseBody
    public void doGet() {
        doProcess("get", null);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{uri}")
    @ResponseBody
    public void doGet(@PathVariable String uri) {
        doProcess("get", uri); // select
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{uri}")
    @ResponseBody
    public void doPost(@PathVariable String uri) {
        doProcess("post", uri); // insert
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{uri}")
    @ResponseBody
    public void doPut(@PathVariable String uri) {
        doProcess("put", uri); // modify
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{uri}")
    @ResponseBody
    public void doDelete(@PathVariable String uri) {
        doProcess("delete", uri);
    }

    private void doProcess(String method, String uri) {
        IAppConfig conf = Application.getAppConfig();
        if (!uri.startsWith("/" + conf.getPathServices()))
            return;

        try {
            req.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        resp.setContentType("text/html;charset=UTF-8");
        ResponseData respData = new ResponseData();

        // 将restPath转成service代码
        DataSet dataIn = new DataSet();
        String str = getParams(req);
        if (null != str && !"[{}]".equals(str))
            dataIn.setJSON(str);
        String serviceCode = getServiceCode(req, method, req.getRequestURI().substring(1), dataIn.getHead());
        log.info(req.getRequestURI() + " => " + serviceCode);
        if (serviceCode == null) {
            respData.setMessage("restful not find: " + req.getRequestURI());
            try {
                resp.getWriter().write(respData.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        log.debug(serviceCode);

        IHandle handle = Application.getHandle();
        try { // 执行指定函数
            handle.init(req.getParameter("token"));
            handle.setProperty(sessionId, req.getSession().getId());
            IService bean = Application.getService(handle, serviceCode);
            if (bean == null) {
                respData.setMessage(String.format("service(%s) is null.", serviceCode));
                resp.getWriter().write(respData.toString());
                return;
            }
            if (!bean.checkSecurity(handle)) {
                respData.setMessage("请您先登入系统");
                resp.getWriter().write(respData.toString());
                return;
            }
            DataSet dataOut = new DataSet();
            IStatus status = bean.execute(dataIn, dataOut);
            respData.setResult(status.getResult());
            respData.setMessage(status.getMessage());
            respData.setData(bean.getJSON(dataOut));
        } catch (Exception e) {
            Throwable err = e.getCause() != null ? e.getCause() : e;
            log.error(err.getMessage(), err);
            respData.setResult(false);
            respData.setMessage(err.getMessage());
        } finally {
            handle.close();
        }
        try {
            resp.getWriter().write(respData.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getServiceCode(HttpServletRequest req, String method, String uri, Record headIn) {
        loadServices(req);
        String[] paths = uri.split("/");
        if (paths.length < 2)
            return null;

        int offset = 0;
        String bookNo = null;
        if (paths.length > 2) {
            if (Utils.isNumeric(paths[1])) {
                offset++;
                bookNo = paths[1];
                headIn.setField("bookNo", bookNo);
                log.info("bookNo:" + bookNo);
            }
        }

        for (String key : services.keySet()) {
            if (!key.startsWith(method + "://"))
                continue;
            int beginIndex = method.length() + 3;
            int endIndex = key.indexOf("?");
            String[] keys;
            String[] params = new String[0];
            if (endIndex > -1) {
                keys = key.substring(beginIndex, endIndex).split("/");
                params = key.substring(endIndex + 1).split("/");
            } else {
                keys = key.substring(beginIndex).split("/");
            }
            if (!"*".equals(keys[0]) && !bookNo.equals(keys[0])) {
                continue;
            }
            if ((keys.length + params.length) != (paths.length - offset))
                continue;
            boolean find = true;
            for (int i = 1; i < keys.length; i++) {
                if (!paths[i + offset].equals(keys[i])) {
                    find = false;
                    break;
                }
            }
            if (find) {
                String serviceCode = services.get(key);
                if (params.length > 0) {
                    for (int i = 0; i < params.length; i++) {
                        String field = params[i];
                        String value = paths[keys.length + i - offset];
                        headIn.setField(field, value);
                    }
                    log.info(serviceCode + ":" + headIn);
                }
                return serviceCode;
            }
        }
        if (paths.length == 2)
            return paths[1];
        return null;
    }

    private String getParams(HttpServletRequest req) {
        BufferedReader reader;
        try {
            reader = req.getReader();
            StringBuffer params = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                params.append(line);
            }
            String result = params.toString();
            return "".equals(result) ? null : result;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private static void loadServices(HttpServletRequest req) {
        if (services != null)
            return;
        services = new HashMap<>();
        for (String serviceCode : Application.get(req).getBeanNamesForType(IRestful.class)) {
            IRestful service = Application.getBean(serviceCode, IRestful.class);
            String path = service.getRestPath();
            if (null != path && !"".equals(path)) {
                services.put(path, serviceCode);
                log.info("restful service " + serviceCode + ": " + path);
            }
        }
    }
}
