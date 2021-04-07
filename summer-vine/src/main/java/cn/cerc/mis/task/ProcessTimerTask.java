package cn.cerc.mis.task;

import java.util.Calendar;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import cn.cerc.core.ISession;
import cn.cerc.core.TDateTime;
import cn.cerc.db.cache.Redis;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.ITokenManage;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.Handle;
import cn.cerc.mis.core.SystemBufferType;
import cn.cerc.mis.rds.StubHandle;

@Component
// 请改使用 StartTaskDefault，注意测试不同用户的创建
public class ProcessTimerTask extends TimerTask implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(ProcessTimerTask.class);
    // 晚上12点执行，也即0点开始执行
    private static final int C_SCHEDULE_HOUR = 0;
    private static boolean isRunning = false;
    private static final String One_O_Clock = "01:00";

    private static String lock;
    private ISession session;

    // 运行环境
    private ApplicationContext context;

    // 循环反复执行
    @Override
    public void run() {
        Calendar calendar = Calendar.getInstance();
        if (!isRunning) {
            isRunning = true;
            if (C_SCHEDULE_HOUR == calendar.get(Calendar.HOUR_OF_DAY)) {
                try {
                    report();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (ServerConfig.enableTaskService()) {
                try {
                    runTask();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            isRunning = false;
        } else {
            log.info("last task has not ended");
        }
    }

    private void runTask() {
        init();

        // 同一秒内，不允许执行2个及以上任务
        String str = TDateTime.now().getTime();
        if (str.equals(lock)) {
            return;
        }
        lock = str;

        for (String beanId : context.getBeanNamesForType(AbstractTask.class)) {
            AbstractTask task = getTask(session, beanId);
            if (task == null) {
                continue;
            }
            try {
                String timeNow = TDateTime.now().getTime().substring(0, 5);
                if (!"".equals(task.getTime()) && !task.getTime().equals(timeNow)) {
                    continue;
                }

                int timeOut = task.getInterval();
                String buffKey = String.format("%d.%s.%s.%s", SystemBufferType.getObject.ordinal(),
                        ServerConfig.getAppName(), this.getClass().getName(), task.getClass().getName());
                if (Redis.get(buffKey) != null) {
                    continue;
                }

                // 标识为已执行
                Redis.set(buffKey, "ok", timeOut);

                if (task.getInterval() > 1) {
                    log.debug("execute task: {}", task.getClass().getName());
                }

                task.execute();
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }
    }

    /**
     * 初始化特殊用户的 handle
     */
    private void init() {
        if (session == null) {
            Application.init(SummerMIS.ID);
            session = Application.createSession();
            // 创建token
            // FIXME 此处需要复查是否存在创建token的必要性
            ITokenManage manage = Application.getBeanDefault(ITokenManage.class, session);
            // 注入token到session
            manage.createToken(StubHandle.DefaultBook, StubHandle.DefaultUser, StubHandle.password,
                    StubHandle.machineCode);
            return;
        }

        // 凌晨1点整重新初始化token
        String now = TDateTime.now().getTime().substring(0, 5);
        if (One_O_Clock.equals(now)) {
            if (Redis.get(now) != null) {
                return;
            }
            if (session != null) {
                session.close();
                session = null;
            }
            log.warn("{} queue reinitialization handle", TDateTime.now());// 队列重新初始化句柄
            Application.init(SummerMIS.ID);
            session = Application.createSession();
            // 创建token
            // FIXME 此处需要复查是否存在创建token的必要性
            ITokenManage manage = Application.getBeanDefault(ITokenManage.class, session);
            manage.createToken(StubHandle.DefaultBook, StubHandle.DefaultUser, StubHandle.password,
                    StubHandle.machineCode);
            // 60s内不重复初始化Handle
            Redis.set(now, "true", 60);
        }
    }

    public static AbstractTask getTask(ISession session, String beanId) {
        AbstractTask task = Application.getBean(AbstractTask.class, beanId);
        if (task != null) {
            task.setSession(session);
            IHandle handle = new Handle(session);
            task.setHandle(handle);
        }
        return task;
    }

    // 每天凌晨开始执行报表或回算任务
    private void report() {
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

}
