package cn.cerc.mis.cache;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.cerc.db.mysql.MysqlServerMaster;
import cn.cerc.db.mysql.MysqlServerSlave;
import cn.cerc.db.redis.JedisFactory;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.BasicHandle;
import cn.cerc.mis.core.SystemBuffer;
import cn.cerc.mis.other.MemoryBuffer;
import redis.clients.jedis.Jedis;

@Component
@WebListener
public class MemoryListener implements ServletContextListener, HttpSessionListener {
    private static final Logger log = LoggerFactory.getLogger(MemoryListener.class);
    public static final String CacheChannel = MemoryBuffer.buildKey(SystemBuffer.Global.CacheReset);
    public static ApplicationContext context;
    private CacheResetMonitor subthread;
    private int count = 0;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        context = WebApplicationContextUtils.getRequiredWebApplicationContext(sce.getServletContext());

        subthread = new CacheResetMonitor();
        subthread.setName("CacheReset-monitor");
        subthread.start();

        MysqlServerMaster.openPool();
        MysqlServerSlave.openPool();

        ApplicationContext context = WebApplicationContextUtils
                .getRequiredWebApplicationContext(sce.getServletContext());
        if (context != null) {
            resetCache(context, CacheResetMode.Start);
        } else {
            log.error("application context null.");
        }
        log.info("tomcat 启动完成");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (subthread != null) {
            subthread.requestStop();
            subthread = null;
        }
        JedisFactory.close();

        // 通知所有的单例重启缓存
        if (context != null) {
            Application.setContext(context);
            for (String beanId : context.getBeanDefinitionNames()) {
                if (context.isSingleton(beanId)) {
                    Object bean = context.getBean(beanId);
                    if (bean instanceof IShutdown) {
                        ((IShutdown) bean).shutdown();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        MysqlServerMaster.closePool();
        MysqlServerSlave.closePool();

        log.info("tomcat 已经关闭");
    }

    @Override
    public synchronized void sessionCreated(HttpSessionEvent se) {
        ++count;
        if (count % 10 == 0)
            log.info("session current size: {}", count);
        log.debug("session MaxInactiveInterval: {}", se.getSession().getMaxInactiveInterval());
        log.debug("session: {}", se.getSession());
        // 过期时间设置，单位为秒
//        se.getSession().setMaxInactiveInterval(30);
    }

    @Override
    public synchronized void sessionDestroyed(HttpSessionEvent se) {
        log.debug("session: {}", se.getSession());
        log.debug("session MaxInactiveInterval: {}", se.getSession().getMaxInactiveInterval());
        --count;
        if (count % 10 == 0)
            log.info("session current size: {}", count);

        if (count != 0)
            return;

        ApplicationContext context = WebApplicationContextUtils
                .getRequiredWebApplicationContext(se.getSession().getServletContext());
        if (context != null) {
            resetCache(context, CacheResetMode.Reset);
        } else {
            log.error("application context null.");
        }
    }

    private void resetCache(ApplicationContext context, CacheResetMode resetType) {
        // 通知所有的单例重启缓存
        Application.setContext(context);
        try (BasicHandle handle = new BasicHandle()) {
            for (String beanId : context.getBeanDefinitionNames()) {
                if (context.isSingleton(beanId)) {
                    Object bean = context.getBean(beanId);
                    if (bean instanceof IMemoryCache) {
                        log.debug("{}.resetCache", beanId);
                        ((IMemoryCache) bean).resetCache(handle, resetType, null);
                    }
                }
            }
        }
    }

    public static void refresh(Class<? extends IMemoryCache> clazz, String param) {
        IMemoryCache cache = Application.getBean(clazz);
        final String beanId = cache.getBeanName();
        try (Jedis jedis = JedisFactory.getJedis()) {
            if (jedis != null)
                jedis.publish(CacheChannel, param == null ? beanId : (beanId + ":" + param));
        }
    }

}
