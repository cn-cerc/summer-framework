package cn.cerc.mis.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

public class RequestScope implements Scope {
    private static final Logger log = LoggerFactory.getLogger(RequestScope.class);
    public static final Map<String, Map<String, Object>> items = new HashMap<>();
    public static final String REQUEST_SCOPE = "request";
    public static final String SESSION_SCOPE = "session";
    private String current = "none";

    @Override
    public Object get(String beanId, ObjectFactory<?> objectFactory) {
        Object bean = getItem().get(beanId);
        if (Objects.isNull(bean)) {
            bean = objectFactory.getObject();
            getItem().put(beanId, bean);
        }
        return bean;
    }

    @Override
    public Object remove(String name) {
        return getItem().remove(name);
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        log.debug("registerDestructionCallback:{}", name);
    }

    @Override
    public Object resolveContextualObject(String key) {
        log.debug("resolveContextualObject:{}", key);
        return null;
    }

    @Override
    public String getConversationId() {
        return Thread.currentThread().getName();
    }

    private Map<String, Object> getItem() {
        Map<String, Object> item = items.get(this.current);
        if (item == null) {
            synchronized (RequestScope.class) {
                log.info("create beans list: {}", this.current);
                item = new HashMap<>();
                items.put(this.current, item);
            }
        }
        return item;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }
}
