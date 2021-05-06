package cn.cerc.mis.core;

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

@Component
@WebListener
public class MemoryListener implements ServletContextListener, HttpSessionListener {
    private static final Logger log = LoggerFactory.getLogger(MemoryListener.class);
    private int count = 0;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("tomcat 启动完成");
        ApplicationContext context = WebApplicationContextUtils
                .getRequiredWebApplicationContext(sce.getServletContext());
        if (context != null) {
            resetCache(context, true);
        } else {
            log.error("application context null.");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("tomcat 已经关闭");
    }

    @Override
    public synchronized void sessionCreated(HttpSessionEvent se) {
        log.info("session current size: {}", ++count);
        log.info("session MaxInactiveInterval: {}", se.getSession().getMaxInactiveInterval());
        log.info("session: {}", se.getSession());
        // 过期时间设置，单位为秒
//        se.getSession().setMaxInactiveInterval(30);
    }

    @Override
    public synchronized void sessionDestroyed(HttpSessionEvent se) {
        log.info("session: {}", se.getSession());
        log.info("session MaxInactiveInterval: {}", se.getSession().getMaxInactiveInterval());
        log.info("session current size: {}", --count);

        if (count != 0)
            return;

        ApplicationContext context = WebApplicationContextUtils
                .getRequiredWebApplicationContext(se.getSession().getServletContext());
        if (context != null) {
            resetCache(context, false);
        } else {
            log.error("application context null.");
        }
    }

    private void resetCache(ApplicationContext context, boolean isFirst) {
        // 通知所有的单例重启缓存
        Application.setContext(context);
        try (BasicHandle handle = new BasicHandle()) {
            for (String beanId : context.getBeanDefinitionNames()) {
                if (context.isSingleton(beanId)) {
                    Object bean = context.getBean(beanId);
                    if (bean instanceof IMemoryCache) {
                        log.info("{}.resetCache", beanId);
                        ((IMemoryCache) bean).resetCache(handle, isFirst);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        ApplicationContext context = Application.initOnlyFramework();
        for (String beanId : context.getBeanDefinitionNames()) {
            if (context.isSingleton(beanId))
                System.out.println(beanId);
        }
    }
}
