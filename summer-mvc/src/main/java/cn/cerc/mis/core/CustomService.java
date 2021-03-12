package cn.cerc.mis.core;

import cn.cerc.core.ClassResource;
import cn.cerc.core.DataSet;
import cn.cerc.db.core.AbstractHandle;
import cn.cerc.db.core.IHandle;
import cn.cerc.mvc.SummerMVC;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class CustomService extends IHandle implements IService, IRestful {
    private static final ClassResource res = new ClassResource(CustomService.class, SummerMVC.ID);

    @Autowired
    public ISystemTable systemTable;
    protected DataSet dataIn = null; // request
    protected DataSet dataOut = null; // response
    protected String funcCode;
    private String message = "";
    private StringBuffer msg = null;
    private String restPath;

    public void init(IHandle handle) {
        this.setHandle(handle);
    }

    public CustomService init(CustomService owner, boolean refData) {
        this.init(owner);
        if (refData) {
            this.dataIn = owner.getDataIn();
            this.dataOut = owner.getDataOut();
        }
        return this;
    }

    public IStatus execute(DataSet dataIn, DataSet dataOut) {
        if (this.funcCode == null) {
            throw new RuntimeException("funcCode is null");
        }
        if (dataIn != null) {
            this.dataIn = dataIn;
        }
        if (dataOut != null) {
            this.dataOut = dataOut;
        }

        ServiceStatus ss = new ServiceStatus(false);
        Class<?> self = this.getClass();
        Method mt = null;
        for (Method item : self.getMethods()) {
            if (item.getName().equals(this.funcCode)) {
                mt = item;
                break;
            }
        }
        if (mt == null) {
            this.setMessage(String.format(res.getString(1, "没有找到服务：%s.%s ！"), this.getClass().getName(), this.funcCode));
            ss.setMessage(this.getMessage());
            ss.setResult(false);
            return ss;
        }

        Webfunc webfunc = mt.getAnnotation(Webfunc.class);
        // if (webfunc == null)
        // log.warn("webfunc not define: " + self.getName() + "." + func);

        try {
            long startTime = System.currentTimeMillis();
            try {
                // 执行具体的服务函数
                if (mt.getParameterCount() == 0) {
                    ss.setResult((Boolean) mt.invoke(this));
                    ss.setMessage(this.getMessage());
                    return ss;
                } else {
                    return (IStatus) mt.invoke(this, dataIn, dataOut);
                }
            } finally {
                if (dataOut != null) {
                    dataOut.first();
                }
                long totalTime = System.currentTimeMillis() - startTime;
                long timeout = webfunc != null ? webfunc.timeout() : 1000;
                if (totalTime > timeout) {
                    String[] tmp = this.getClass().getName().split("\\.");
                    String service = tmp[tmp.length - 1] + "." + this.funcCode;
                    log.warn(String.format("corpNo:%s, userCode:%s, service:%s, tickCount:%s", getCorpNo(),
                            getUserCode(), service, totalTime));
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            Throwable err = e.getCause() != null ? e.getCause() : e;
            String msg = err.getMessage() == null ? "error is null" : err.getMessage();
            if ((err instanceof ServiceException)) {
                this.setMessage(msg);
                ss.setMessage(msg);
                ss.setResult(false);
                return ss;
            } else {
                log.error(msg, err);
                this.setMessage(msg);
                ss.setMessage(msg);
                ss.setResult(false);
                return ss;
            }
        }
    }

    public DataSet getDataIn() {
        if (dataIn == null) {
            dataIn = new DataSet();
        }
        return dataIn;
    }

    public DataSet getDataOut() {
        if (dataOut == null) {
            dataOut = new DataSet();
        }
        return dataOut;
    }

    // 需要返回的失败讯息, 且永远为 false !
    public boolean fail(String text) {
        this.setMessage(text);
        return false;
    }

    public StringBuffer getMsg() {
        if (msg == null) {
            msg = new StringBuffer(message);
        }
        return msg;
    }

    public String getMessage() {
        return msg != null ? msg.toString() : message;
    }

    public void setMessage(String message) {
        if (message == null || "".equals(message.trim())) {
            return;
        }
        if (msg != null) {
            this.msg.append(message);
        } else {
            this.message = message;
        }
    }

    public String getJSON(DataSet dataOut) {
        return String.format("[%s]", this.getDataOut().getJSON());
    }

    // 设置是否需要授权才能登入
    public boolean checkSecurity(IHandle handle) {
        IHandle sess = (IHandle) handle.getProperty(null);
        return sess != null && sess.logon();
    }

    public String getFuncCode() {
        return funcCode;
    }

    public void setFuncCode(String funcCode) {
        this.funcCode = funcCode;
    }

    @Override
    public String getRestPath() {
        return restPath;
    }

    @Override
    public void setRestPath(String restPath) {
        this.restPath = restPath;
    }
}
