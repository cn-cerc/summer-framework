package cn.cerc.mis.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.ClassResource;
import cn.cerc.core.DataSet;
import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.core.Utils;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.ITokenManage;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.Handle;
import cn.cerc.mis.core.IRestful;
import cn.cerc.mis.core.IService;
import cn.cerc.mis.core.IStatus;

@Deprecated // 请改使用 StartServiceDefault
public class StartServices extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(StartServices.class);
    private static final ClassResource res = new ClassResource(StartServices.class, SummerMIS.ID);
    private static final ClassConfig config = new ClassConfig(StartServices.class, SummerMIS.ID);

    private static final long serialVersionUID = 1L;
    private static final String sessionId = "sessionId";
    private static Map<String, String> services;
    public final String outMsg = "{\"result\":%s,\"message\":\"%s\"}";

    private static void loadServices(HttpServletRequest req) {
        if (services != null) {
            return;
        }
        services = new HashMap<>();
        for (String serviceCode : Application.get(req).getBeanNamesForType(IRestful.class)) {
            IRestful service = Application.getBean(IRestful.class, serviceCode);
            String path = service.getRestPath();
            if (null != path && !"".equals(path)) {
                services.put(path, serviceCode);
                log.info("restful service " + serviceCode + ": " + path);
            }
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doProcess("get", req, resp); // select
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doProcess("post", req, resp); // insert
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doProcess("put", req, resp); // modify
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doProcess("delete", req, resp);
    }

    private void doProcess(String method, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uri = req.getRequestURI();
        if (!uri.startsWith("/" + config.getString(Application.PATH_SERVICES, "services"))) {
            return;
        }

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        ResponseData respData = new ResponseData();

        // 将restPath转成service代码
        DataSet dataIn = new DataSet();
        String str = getParams(req);
        if (null != str && !"[{}]".equals(str)) {
            dataIn.setJSON(str);
        }
        String serviceCode = getServiceCode(req, method, req.getRequestURI().substring(1), dataIn.getHead());
        log.info(req.getRequestURI() + " => " + serviceCode);
        if (serviceCode == null) {
            respData.setMessage("restful not find: " + req.getRequestURI());
            resp.getWriter().write(respData.toString());
            return;
        }
        log.debug(serviceCode);

        ISession session = Application.createSession();
        try { // 执行指定函数
            ITokenManage manage = Application.getBeanDefault(ITokenManage.class, session);
            manage.resumeToken(req.getParameter("token"));
            session.setProperty(sessionId, req.getSession().getId());
            IHandle handle = new Handle(session);
            IService bean = Application.getService(handle, serviceCode);

            if (!bean.checkSecurity(handle)) {
                respData.setMessage(res.getString(1, "请您先登入系统"));
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
            session.close();
        }
        resp.getWriter().write(respData.toString());
    }

    public String getServiceCode(HttpServletRequest req, String method, String uri, Record headIn) {
        loadServices(req);
        String[] paths = uri.split("/");
        if (paths.length < 2) {
            return null;
        }

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
            if (!key.startsWith(method + "://")) {
                continue;
            }
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
            if ((keys.length + params.length) != (paths.length - offset)) {
                continue;
            }
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
        if (paths.length == 2) {
            return paths[1];
        }
        return null;
    }

    private String getParams(HttpServletRequest req) {
        BufferedReader reader;
        try {
            reader = req.getReader();
            StringBuffer params = new StringBuffer();
            String line;
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
}
