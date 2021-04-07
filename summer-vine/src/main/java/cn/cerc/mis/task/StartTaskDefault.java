package cn.cerc.mis.task;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;

import cn.cerc.core.ISession;
import cn.cerc.core.TDateTime;
import cn.cerc.db.cache.Redis;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.SystemBufferType;

public class StartTaskDefault implements Runnable, ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(StartTaskDefault.class);
    // 晚上12点执行，也即0点开始执行
    private static final int C_SCHEDULE_HOUR = 0;
    private static boolean isRunning = false;
    private static String lock;
    private ApplicationContext context;

    public static AbstractTask getTask(ISession session, String beanId) {
        AbstractTask task = Application.getBean(AbstractTask.class, beanId);
        if (task != null) {
            task.setSession(session);
        }
        return task;
    }

    // 循环反复执行
    @Override
    @Scheduled(fixedRate = 500)
    public void run() {
        Calendar c = Calendar.getInstance();
        if (!isRunning) {
            isRunning = true;
            if (C_SCHEDULE_HOUR == c.get(Calendar.HOUR_OF_DAY)) {
                try {
                    report();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (ServerConfig.enableTaskService()) {
                try {
                    Application.init(SummerMIS.ID);
                    ISession session = Application.createSession();
                    runTask(session);
                    session.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            isRunning = false;
        } else {
            log.info("last task has not ended");
        }
    }

    // 每天凌晨开始执行报表或回算任务
    private void report() {
        return;
    }

    private void runTask(ISession session) {
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
                String curTime = TDateTime.now().getTime().substring(0, 5);
                if (!"".equals(task.getTime()) && !task.getTime().equals(curTime)) {
                    continue;
                }

                int timeOut = task.getInterval();
                String buffKey = String.format("%d.%s.%s", SystemBufferType.getObject.ordinal(),
                        this.getClass().getName(), task.getClass().getName());
                if (Redis.get(buffKey) != null) {
                    continue;
                }

                // 标识为已执行
                Redis.set(buffKey, "ok", timeOut);

                if (task.getInterval() > 1) {
                    log.info("execute " + task.getClass().getName());
                }

                task.execute();
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
