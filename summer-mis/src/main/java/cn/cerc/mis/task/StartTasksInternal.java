package cn.cerc.mis.task;

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import cn.cerc.db.core.ServerConfig;
import cn.cerc.mis.core.Application;

//使用内部驱动定时任务
@Deprecated // 请改使用 StartTaskDefault
public class StartTasksInternal implements ServletContextListener {
    private static final int step = 500;
    private Timer timer = null;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        if (ServerConfig.enableTaskService()) {
            timer = new Timer(true);
            event.getServletContext().log("定时器已启动");
            Application.get(event.getServletContext());
            ProcessTimerTask obj = new ProcessTimerTask();
            obj.setApplicationContext(Application.getContext());
            // 3秒后开始启动，3*1000表示每隔3秒执行任务
            timer.schedule(obj, 3 * 1000, step);
            event.getServletContext().log("已经添加任务");
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
