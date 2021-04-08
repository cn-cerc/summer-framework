package cn.cerc.mis.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cn.cerc.core.ClassResource;
import cn.cerc.core.DataSet;
import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;
import cn.cerc.mis.SummerMIS;

public class CustomService implements IMultiplService, IRestful {
    private static final Logger log = LoggerFactory.getLogger(CustomService.class);
    private static final ClassResource res = new ClassResource(CustomService.class, SummerMIS.ID);

    @Autowired
    public ISystemTable systemTable;
    protected DataSet dataIn = null; // request
    protected DataSet dataOut = null; // response
    protected String funcCode;
    private String message = "";
    private StringBuffer msg = null;
    private String restPath;
    private ISession session;
    protected IHandle handle;

    public CustomService init(CustomService owner, boolean refData) {
        this.setHandle(owner);
        if (refData) {
            this.dataIn = owner.getDataIn();
            this.dataOut = owner.getDataOut();
        }
        return this;
    }

    @Override
    public IStatus execute(DataSet dataIn, DataSet dataOut) throws ServiceException {
        this.setDataIn(dataIn);
        this.setDataOut(dataOut);

        if (this.funcCode == null) {
            throw new RuntimeException("funcCode is null");
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
            this.setMessage(
                    String.format(res.getString(1, "没有找到服务：%s.%s ！"), this.getClass().getName(), this.funcCode));
            ss.setMessage(this.getMessage());
            ss.setResult(false);
            return ss;
        }

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
                long timeout = 1000;
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

    @Override
    public DataSet getDataIn() {
        if (dataIn == null) {
            dataIn = new DataSet();
        }
        return dataIn;
    }

    @Override
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

    @Deprecated
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

    @Override
    public String getJSON(DataSet dataOut) {
        return String.format("[%s]", this.getDataOut().getJSON());
    }

    // 设置是否需要授权才能登入
    @Override
    public boolean checkSecurity(IHandle handle) {
        ISession sess = handle.getSession();
        return sess != null && sess.logon();
    }

    @Override
    public String getFuncCode() {
        return funcCode;
    }

    @Override
    public void setFuncCode(String funcCode) {
        this.funcCode = funcCode;
    }

    @Override
    public String getCorpNo() {
        if (handle != null)
            return handle.getCorpNo();
        else
            return getSession().getCorpNo();
    }

    @Override
    public void setDataIn(DataSet dataIn) {
        this.dataIn = dataIn;
    }

    @Override
    public void setDataOut(DataSet dataOut) {
        this.dataOut = dataOut;
    }

    @Override
    public String getRestPath() {
        return restPath;
    }

    @Override
    public void setRestPath(String restPath) {
        this.restPath = restPath;
    }

    @Override
    public ISession getSession() {
        return session;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
        if (handle == null)
            handle = new Handle(session);
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

}
