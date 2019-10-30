package cn.cerc.mis.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.DataSet;
import cn.cerc.core.IHandle;
import cn.cerc.core.MD5;
import cn.cerc.core.Record;
import cn.cerc.db.cache.Redis;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.mis.client.IServiceProxy;
import cn.cerc.mis.client.Microservice;
import cn.cerc.mis.other.BufferType;
import cn.cerc.mis.other.MemoryBuffer;

public class LocalService implements IServiceProxy {
    private static final Logger log = LoggerFactory.getLogger(LocalService.class);
    private String serviceCode;

    private String message;
    private IHandle handle;
    // 是否激活缓存
    private boolean bufferRead = true;
    private boolean bufferWrite = true;

    private DataSet dataIn = new DataSet();
    private DataSet dataOut = new DataSet();

    public LocalService(IHandle handle) {
        this.handle = handle;
        if (handle == null)
            throw new RuntimeException("handle is null.");

        String pageNo = null;
        HttpServletRequest req = (HttpServletRequest) handle.getProperty("request");
        if (req != null)
            pageNo = req.getParameter("pageno");

        // 遇到分页符时，尝试读取缓存
        this.bufferRead = pageNo != null;
    }

    public LocalService(IHandle handle, String service) {
        this(handle);
        this.setService(service);
    }

    @Override
    public String getService() {
        return serviceCode;
    }

    @Override
    public IServiceProxy setService(String service) {
        this.serviceCode = service;
        return this;
    }

    @Override
    public String getMessage() {
        if (message != null) {
            return message.replaceAll("'", "\"");
        } else {
            return null;
        }
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // 带缓存调用服务
    @Override
    public boolean exec(Object... args) {
        if (args.length > 0) {
            Record headIn = getDataIn().getHead();
            if (args.length % 2 != 0)
                throw new RuntimeException("传入的参数数量必须为偶数！");
            for (int i = 0; i < args.length; i = i + 2)
                headIn.setField(args[i].toString(), args[i + 1]);
        }
        if (handle == null)
            throw new RuntimeException("handle is null.");
        if (serviceCode == null)
            throw new RuntimeException("service is null.");

        IService bean = Application.getService(handle, serviceCode);
        if (bean == null) {
            this.message = String.format("bean %s not find", serviceCode);
            return false;
        }
        if ((bean instanceof Microservice) && ((Microservice) bean).getService() == null)
            ((Microservice) bean).setService(serviceCode);

        try {
            if (!"AppSessionRestore.byUserCode".equals(this.serviceCode)
                    && !"SvrUserMessages.getWaitList".equals(this.serviceCode)) {
                log.info(this.serviceCode);
            }
            if (ServerConfig.isServerMaster()) {
                IStatus status = bean.execute(dataIn, dataOut);
                boolean result = status.getResult();
                message = status.getMessage();
                return result;
            }

            // 制作临时缓存Key
            String key = MD5.get(handle.getUserCode() + this.serviceCode + dataIn.getJSON());

            if (bufferRead) {
                String buffValue = Redis.get(key);
                if (buffValue != null) {
                    log.debug("read from buffer: " + this.serviceCode);
                    dataOut.setJSON(buffValue);
                    message = dataOut.getHead().getString("_message_");
                    return dataOut.getHead().getBoolean("_result_");
                }
            }

            // 没有缓存时，直接读取并存入缓存
            bean.init(handle);
            IStatus status = bean.execute(dataIn, dataOut);
            boolean result = status.getResult();
            message = status.getMessage();

            if (bufferWrite) {
                log.debug("write to buffer: " + this.serviceCode);
                dataOut.getHead().setField("_message_", message);
                dataOut.getHead().setField("_result_", result);
                Redis.set(key, dataOut.getJSON());
            }
            return result;
        } catch (Exception e) {
            Throwable err = e;
            if (e.getCause() != null)
                err = e.getCause();
            log.error(err.getMessage(), err);
            message = err.getMessage();
            return false;
        }
    }

    // 不带缓存调用服务
    public IStatus execute(Object... args) {
        if (args.length > 0) {
            Record headIn = getDataIn().getHead();
            if (args.length % 2 != 0)
                return new ServiceStatus(false, "传入的参数数量必须为偶数！");
            for (int i = 0; i < args.length; i = i + 2)
                headIn.setField(args[i].toString(), args[i + 1]);
        }
        if (handle == null)
            return new ServiceStatus(false, "handle is null.");
        if (serviceCode == null)
            return new ServiceStatus(false, "service is null.");

        IService bean = Application.getService(handle, serviceCode);
        if (bean == null)
            return new ServiceStatus(false, String.format("bean %s not find", serviceCode));
        if ((bean instanceof Microservice) && ((Microservice) bean).getService() == null)
            ((Microservice) bean).setService(serviceCode);

        try {
            log.info(this.serviceCode);
            IStatus status = bean.execute(dataIn, dataOut);
            message = status.getMessage();
            return status;
        } catch (Exception e) {
            Throwable err = e;
            if (e.getCause() != null)
                err = e.getCause();
            log.error(err.getMessage(), err);
            message = err.getMessage();
            return new ServiceStatus(false, message);
        }
    }

    @Override
    public DataSet getDataOut() {
        return this.dataOut;
    }

    @Override
    public DataSet getDataIn() {
        return this.dataIn;
    }

    public String getExportKey() {
        String tmp = "" + System.currentTimeMillis();
        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getExportKey, handle.getUserCode(), tmp)) {
            buff.setField("data", this.getDataIn().getJSON());
        }
        return tmp;
    }

    public LocalService setBufferRead(boolean bufferRead) {
        this.bufferRead = bufferRead;
        return this;
    }

    public LocalService setBufferWrite(boolean bufferWrite) {
        this.bufferWrite = bufferWrite;
        return this;
    }

    public static void listMethod(Class<?> clazz) {
        Map<String, Class<?>> items = new HashMap<>();
        String[] args = clazz.getName().split("\\.");
        String classCode = args[args.length - 1];
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getReturnType().getName().equals("boolean")) {
                if (method.getParameters().length == 0) {
                    String name = method.getName();
                    if (method.getName().startsWith("_"))
                        name = name.substring(1, name.length());
                    items.put(classCode + "." + name, clazz);
                }
            }
        }
        for (String key : items.keySet()) {
            log.info(key);
        }
    }

}
