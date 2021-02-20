package cn.cerc.mis.task;

import cn.cerc.mis.core.AbstractHandle;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.HandleDefault;
import cn.cerc.mis.core.ISystemTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractTask extends AbstractHandle implements Runnable {

    @Autowired
    public ISystemTable systemTable;
    private String describe;

    /**
     * 缓存过期时间
     * 单位：秒
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
        HandleDefault handle = new HandleDefault();
        try {
            this.setHandle(handle);
            handle.setProperty(Application.userCode, "admin");
            this.execute();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            handle.close();
        }
    }

    // 具体业务逻辑代码
    public abstract void execute() throws Exception;
}
