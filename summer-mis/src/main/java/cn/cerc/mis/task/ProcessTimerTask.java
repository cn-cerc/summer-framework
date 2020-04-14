package cn.cerc.mis.task;

import cn.cerc.core.IHandle;
import cn.cerc.core.TDateTime;
import cn.cerc.db.cache.Redis;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.other.BufferType;
import cn.cerc.mis.rds.StubHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.TimerTask;

@Slf4j
@Deprecated // 请改使用 StartTaskDefault
public class ProcessTimerTask extends TimerTask implements ApplicationContextAware {

    // 晚上12点执行，也即0点开始执行
    private static final int C_SCHEDULE_HOUR = 0;
    private static boolean isRunning = false;
    private static final String One_O_Clock = "01:00:00";

    private static String lock;
    private IHandle handle;

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
            log.info("上一次任务执行还未结束");
        }
    }

    private void runTask() {
        init();

        // 同一秒内，不允许执行2个及以上任务
        String str = TDateTime.Now().getTime();
        if (str.equals(lock)) {
            return;
        }
        lock = str;

        for (String beanId : context.getBeanNamesForType(AbstractTask.class)) {
            AbstractTask task = getTask(handle, beanId);
            if (task == null) {
                continue;
            }
            try {
                String timeNow = TDateTime.Now().getTime().substring(0, 5);
                if (!"".equals(task.getTime()) && !task.getTime().equals(timeNow)) {
                    continue;
                }

                int timeOut = task.getInterval();
                String buffKey = String.format("%d.%s.%s.%s", BufferType.getObject.ordinal(), ServerConfig.getAppName(), this.getClass().getName(), task.getClass().getName());
                if (Redis.get(buffKey) != null) {
                    continue;
                }

                // 标识为已执行
                Redis.set(buffKey, "ok", timeOut);

                if (task.getInterval() > 1) {
                    log.debug("执行任务 {}", task.getClass().getName());
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
        if (handle == null) {
            handle = new StubHandle();
        }

        // 凌晨1点整重新初始化token
        LocalTime now = LocalTime.now().withNano(0);
        if (One_O_Clock.equals(now.toString())) {
            if (handle != null) {
                handle.close();
                handle = null;
            }
            log.warn("{} 队列重新初始化句柄", TDateTime.Now());
            handle = new StubHandle();
        }
    }

    public static AbstractTask getTask(IHandle handle, String beanId) {
        AbstractTask task = Application.getBean(beanId, AbstractTask.class);
        if (task != null) {
            task.setHandle(handle);
        }
        return task;
    }

    // 每天凌晨开始执行报表或回算任务
    private void report() {
        return;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

}
