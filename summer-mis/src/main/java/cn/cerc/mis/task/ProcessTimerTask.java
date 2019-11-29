package cn.cerc.mis.task;

import java.util.Calendar;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import cn.cerc.core.IHandle;
import cn.cerc.core.TDateTime;
import cn.cerc.db.cache.Redis;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.other.BufferType;
import cn.cerc.mis.rds.StubHandle;

@Deprecated // 请改使用 StartTaskDefault
public class ProcessTimerTask extends TimerTask implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(ProcessTimerTask.class);
    private static boolean isRunning = false;
    // 晚上12点执行，也即0点开始执行
    private static final int C_SCHEDULE_HOUR = 0;
    private static String lock;
    // 运行环境
    private ApplicationContext context;

    // 循环反复执行
    @Override
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
                    StubHandle handle = new StubHandle();
                    runTask(handle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            isRunning = false;
        } else {
            log.info("上一次任务执行还未结束");
        }
    }

    // 每天凌晨开始执行报表或回算任务
    private void report() {
        return;
    }

    private void runTask(IHandle handle) {
        // 同一秒内，不允许执行2个及以上任务
        String str = TDateTime.Now().getTime();
        if (str.equals(lock))
            return;

        lock = str;
        for (String beanId : context.getBeanNamesForType(AbstractTask.class)) {
            AbstractTask task = getTask(handle, beanId);
            if (task == null)
                continue;
            try {
                String curTime = TDateTime.Now().getTime().substring(0, 5);
                if (!"".equals(task.getTime()) && !task.getTime().equals(curTime))
                    continue;

                int timeOut = task.getInterval();
                String buffKey = String.format("%d.%s.%s", BufferType.getObject.ordinal(), this.getClass().getName(),
                        task.getClass().getName());
                if (Redis.get(buffKey) != null)
                    continue;

                // 标识为已执行
                Redis.set(buffKey, "ok", timeOut);

                if (task.getInterval() > 1)
                    log.info("execute " + task.getClass().getName());

                task.execute();
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }
    }

    public static AbstractTask getTask(IHandle handle, String beanId) {
        AbstractTask task = Application.getBean(beanId, AbstractTask.class);
        if (task != null)
            task.setHandle(handle);
        return task;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }
}
