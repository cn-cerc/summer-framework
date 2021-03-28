package cn.cerc.mis.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import cn.cerc.core.DataSet;
import cn.cerc.core.MD5;
import cn.cerc.core.Record;
import cn.cerc.db.cache.Redis;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.mis.client.IServiceProxy;
import cn.cerc.mis.other.MemoryBuffer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalService extends CustomLocalProxy implements IServiceProxy {
    // 是否激活缓存
    private boolean bufferRead = true;
    private boolean bufferWrite = true;

    private DataSet dataIn = new DataSet();
    private DataSet dataOut = new DataSet();

    public LocalService(IHandle handle) {
        super(handle);
        String pageNo = null;
        HttpServletRequest req = (HttpServletRequest) handle.getProperty("request");
        if (req != null) {
            pageNo = req.getParameter("pageno");
        }

        // 遇到分页符时，尝试读取缓存
        this.bufferRead = pageNo != null;
    }

    public LocalService(IHandle handle, String service) {
        this(handle);
        this.setService(service);
    }

    @Deprecated
    public static void listMethod(Class<?> clazz) {
        Map<String, Class<?>> items = new HashMap<>();
        String[] args = clazz.getName().split("\\.");
        String classCode = args[args.length - 1];
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if ("boolean".equals(method.getReturnType().getName())) {
                if (method.getParameters().length == 0) {
                    String name = method.getName();
                    if (method.getName().startsWith("_")) {
                        name = name.substring(1);
                    }
                    items.put(classCode + "." + name, clazz);
                }
            }
        }
        for (String key : items.keySet()) {
            log.info(key);
        }
    }

    // 带缓存调用服务
    @Override
    public boolean exec(Object... args) {
        if (args.length > 0) {
            Record headIn = getDataIn().getHead();
            if (args.length % 2 != 0) {
                // TODO 此处应该使用 ClassResource
                throw new RuntimeException("传入的参数数量必须为偶数！");
            }
            for (int i = 0; i < args.length; i = i + 2) {
                headIn.setField(args[i].toString(), args[i + 1]);
            }
        }

        Object object = getServiceObject();
        if (object == null) {
            return false;
        }

        try {
            if (!"SvrSession.byUserCode".equals(this.getService())
                    && !"SvrUserMessages.getWaitList".equals(this.getService())) {
                log.debug(this.getService());
            }
            
            if (ServerConfig.isServerMaster()) {
                return executeService(object, this.dataIn, this.dataOut);
            }

            // 制作临时缓存Key
            String key = MD5.get(getHandle().getUserCode() + this.getService() + dataIn.getJSON());
            
            if (bufferRead) {
                String buffValue = Redis.get(key);
                if (buffValue != null) {
                    log.debug("read from buffer: " + this.getService());
                    dataOut.setJSON(buffValue);
                    setMessage(dataOut.getHead().getString("_message_"));
                    return dataOut.getHead().getBoolean("_result_");
                }
            }

            // 没有缓存时，直接读取并存入缓存
            Boolean result = executeService(object, this.dataIn, this.dataOut);
            if (bufferWrite) {
                log.debug("write to buffer: " + this.getService());
                dataOut.getHead().setField("_message_", this.getMessage());
                dataOut.getHead().setField("_result_", result);
                Redis.set(key, dataOut.getJSON());
            }
            return result;
        } catch (Exception e) {
            Throwable err = e;
            if (e.getCause() != null) {
                err = e.getCause();
            }
            log.error(err.getMessage(), err);
            setMessage(err.getMessage());
            return false;
        }
    }

    // 不带缓存调用服务
    public IStatus execute(Object... args) {
        if (args.length > 0) {
            Record headIn = getDataIn().getHead();
            if (args.length % 2 != 0) {
                // TODO 此处应该使用ClassResource
                return new ServiceStatus(false, "传入的参数数量必须为偶数！");
            }
            for (int i = 0; i < args.length; i = i + 2) {
                headIn.setField(args[i].toString(), args[i + 1]);
            }
        }

        Object object = getServiceObject();
        if (object == null) {
            return new ServiceStatus(false, this.getMessage());
        }

        try {
            log.debug(this.getService());
            ServiceStatus status = new ServiceStatus();
            status.setResult(executeService(object, this.dataIn, this.dataOut));
            status.setMessage(this.getMessage());
            return status;
        } catch (Exception e) {
            Throwable err = e;
            if (e.getCause() != null) {
                err = e.getCause();
            }
            log.error(err.getMessage(), err);
            setMessage(err.getMessage());
            return new ServiceStatus(false, this.getMessage());
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
        try (MemoryBuffer buff = new MemoryBuffer(SystemBufferType.getExportKey, getHandle().getUserCode(), tmp)) {
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

}
