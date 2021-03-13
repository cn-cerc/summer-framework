package cn.cerc.mis.task;

import org.springframework.beans.factory.annotation.Autowired;

import cn.cerc.core.ISession;
import cn.cerc.db.core.CustomBean;
import cn.cerc.db.core.ITokenManage;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.HandleDefault;
import cn.cerc.mis.core.ISystemTable;
import cn.cerc.mis.rds.StubHandle;
import cn.cerc.mvc.SummerMVC;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractTask extends CustomBean implements Runnable {

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
        Application.init(SummerMVC.ID);
        ISession session = Application.createSession();
        try {
            session.setProperty(Application.bookNo, StubHandle.DefaultBook);
            session.setProperty(Application.userCode, "admin");
            // 创建token
            ITokenManage manage = Application.getBeanDefault(ITokenManage.class, session);
            manage.createToken(StubHandle.DefaultBook, StubHandle.DefaultUser, StubHandle.password,
                    StubHandle.machineCode);
            // 开始执行
            this.setHandle(new HandleDefault(session));
            this.execute();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    // 具体业务逻辑代码
    public abstract void execute() throws Exception;
}
