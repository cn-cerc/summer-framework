package cn.cerc.mis.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cn.cerc.core.ISession;
import cn.cerc.db.core.Handle;
import cn.cerc.mis.core.BasicHandle;
import cn.cerc.mis.core.ISystemTable;

public abstract class AbstractTask extends Handle implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(AbstractTask.class);
    private BasicHandle handle;
    private int sessionTimes = 0;

    @Autowired
    public ISystemTable systemTable;
    private String describe;

    /**
     * 缓存过期时间 单位：秒
     **/
    private int interval;
    private String time = "";

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    /**
     * 外部执行入口
     */
    @Override
    public void run() {
        if (sessionTimes == 0) {
            if (handle != null)
                handle.close();

            sessionTimes = 3600;
            handle = new BasicHandle();
        }

        try {
            this.setSession(handle.getSession());
            handle.getSession().setProperty(ISession.USER_CODE, "admin");
            this.execute();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            sessionTimes--;
        }
    }

    // 具体业务逻辑代码
    public abstract void execute() throws Exception;
}
