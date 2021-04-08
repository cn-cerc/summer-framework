package cn.cerc.mis.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.DataSet;
import cn.cerc.core.MD5;
import cn.cerc.core.Record;
import cn.cerc.db.cache.Redis;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.db.core.IHandleOwner;
import cn.cerc.mis.client.IServiceProxy;
import cn.cerc.mis.other.MemoryBuffer;

public class LocalService extends CustomLocalProxy implements IServiceProxy {
    private static final Logger log = LoggerFactory.getLogger(LocalService.class);
    // 是否激活缓存
    private boolean bufferRead = true;
    private boolean bufferWrite = true;

    private DataSet dataIn;
    private DataSet dataOut;

    public LocalService(IHandle handle) {
        super(handle);
        String pageNo = null;
        HttpServletRequest req = (HttpServletRequest) handle.getSession().getProperty("request");
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

    // 带缓存调用服务
    @Override
    public boolean exec(Object... args) {
        if (this.dataIn == null)
            this.dataIn = new DataSet();
        if (this.dataOut == null)
            this.dataOut = new DataSet();

        initDataIn(args);

        Object object = getServiceObject();
        if (object == null) {
            return false;
        }

        try {
            if (!"SvrSession.byUserCode".equals(this.getService())
                    && !"SvrUserMessages.getWaitList".equals(this.getService())) {
                log.debug(this.getService());
            }
            if (object instanceof IHandleOwner) {
                ((IHandleOwner) object).setHandle(this.getHandle());
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

    private void initDataIn(Object... args) {
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
    }

    public String getExportKey() {
        String tmp = "" + System.currentTimeMillis();
        try (MemoryBuffer buff = new MemoryBuffer(SystemBuffer.User.ExportKey, getHandle().getUserCode(), tmp)) {
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

    @Override
    public DataSet getDataOut() {
        if (this.dataOut == null)
            this.dataOut = new DataSet();
        return this.dataOut;
    }

    @Override
    public DataSet getDataIn() {
        if (this.dataIn == null)
            this.dataIn = new DataSet();
        return this.dataIn;
    }

    public void setDataIn(DataSet dataIn) {
        this.dataIn = dataIn;
    }

    public void setDataOut(DataSet dataOut) {
        this.dataOut = dataOut;
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

}
