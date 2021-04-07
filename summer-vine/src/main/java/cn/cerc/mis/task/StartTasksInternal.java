package cn.cerc.mis.task;

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.db.core.ServerConfig;
import cn.cerc.mis.core.Application;

//使用内部驱动定时任务
public class StartTasksInternal implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(StartTasksInternal.class);
    private static final long period = 500;
    private static final long delay = 3 * 1000;
    private Timer timer = null;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        if (ServerConfig.enableTaskService()) {
            timer = new Timer(true);
            log.info("Timer started ...");

            Application.get(event.getServletContext());
            ProcessTimerTask task = new ProcessTimerTask();
            task.setApplicationContext(Application.getContext());

            /*
             * 1、项目启动完成3秒后开始启动task 2、每次执行完成一个，500毫秒以后开始执行下一个
             */
            timer.schedule(task, delay, period);
            log.info("Task has been added ...");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (timer != null) {
            timer.cancel();
            event.getServletContext().log("Timer is broken ...");
        }
    }
}
