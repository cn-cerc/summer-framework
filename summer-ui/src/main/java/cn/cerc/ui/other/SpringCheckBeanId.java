package cn.cerc.ui.other;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.CustomService;
import cn.cerc.ui.parts.UIComponent;

public class SpringCheckBeanId {
    private static final Logger log = LoggerFactory.getLogger(SpringCheckBeanId.class);
    private Map<String, String> items = new HashMap<>();
    private ApplicationContext context;

    public void run(boolean mutiName) {
        context = Application.getContext();
        Set<String> lines = new HashSet<>();
        for (String beanId : context.getBeanDefinitionNames()) {
            if (!"appClient".equals(beanId)) {
                check(beanId, lines);
            }
        }
        if (!mutiName)
            return;
        for (String className : items.keySet()) {
            log.info("{}, beanId 名字有多个：{}", className, items.get(className));
        }
    }

    private void check(String beanId, Set<String> lines) {
        Object object;
        try {
            object = context.getBean(beanId);
            checkType(lines, object);
        } catch (Exception e) {
            log.error("create beanId: {}", beanId);
            e.printStackTrace();
        }
    }

    private void checkType(Set<String> lines, Object classType) {
        String items[] = context.getBeanNamesForType(classType.getClass());
        if (items.length == 0) {
            log.error("未实现类：{}", classType.getClass().getName());
            return;
        }
        if (items.length == 1)
            return;

        for (String beanId : items) {
            appendBeanId(classType.getClass().getName(), beanId);
            String[] path = beanId.split("\\.");
            if (path.length == 1) {
                continue;
            }

            if (path.length > 2) {
                log.error("beanId: {}，命名格式应为：ClassName.FuncCode", beanId);
            }

            Object object = context.getBean(beanId);
            String[] args = object.getClass().getName().split("\\.");
            String className = args[args.length - 1];

            if (!path[0].equals(className)) {
                log.warn("{}, beanId: {} 与类名不一致", object.getClass().getName(), beanId);
            }
            
            if(object instanceof UIComponent)
                continue;

            String funcName = path[1];
            if (!(object instanceof CustomService)) {
                log.error("命名异常：{}，非CustomService子类", beanId);
            }
            CustomService svr = (CustomService) object;
            if (!funcName.equals(svr.getFuncCode()))
                log.warn("{}, 函数名注册为: {}，实际为：{}", beanId, funcName, svr.getFuncCode());
        }
    }

    private void appendBeanId(String className, String beanId) {
        if (items.containsKey(className)) {
            String value = items.get(className);
            if (value.indexOf(beanId) == -1)
                value += "," + beanId;
            items.put(className, value);
        } else {
            items.put(className, beanId);
        }
    }

    public static void main(String[] args) {
        // 此下代码须在项目中执行，以便其加载application.xml文件！
        // 用于检测命名异常，修复后可以删除app-services.xml之类的文件
        Application.init();
        new SpringCheckBeanId().run(true);
    }

}
