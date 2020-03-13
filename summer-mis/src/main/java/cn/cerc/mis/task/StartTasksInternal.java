package cn.cerc.mis.task;

import cn.cerc.db.core.ServerConfig;
import cn.cerc.mis.core.Application;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Timer;

//使用内部驱动定时任务
@Deprecated // 请改使用 StartTaskDefault
@Slf4j
public class StartTasksInternal implements ServletContextListener {

    private static final long period = 500;
    private static final long delay = 3 * 1000;
    private Timer timer = null;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        if (ServerConfig.enableTaskService()) {
            timer = new Timer(true);
            log.info("定时器已启动");

            Application.get(event.getServletContext());
            ProcessTimerTask task = new ProcessTimerTask();
            task.setApplicationContext(Application.getContext());

            /**
             * 1、项目启动完成3秒后开始启动task
             * 2、每次执行完成一个，500毫秒以后开始执行下一个
             */
            timer.schedule(task, delay, period);

            log.info("已经添加任务");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (timer != null) {
            timer.cancel();
            event.getServletContext().log("定时器销毁");
        }
    }
}
