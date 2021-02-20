package cn.cerc.ui.mvc;

import cn.cerc.ui.vcl.UIText;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class FieldMark {
    private static ApplicationContext app;
    private static String xmlFile = "classpath:field-mark.xml";

    public static UIText get(String beanId) {
        if (app == null) {
            app = new FileSystemXmlApplicationContext(xmlFile);
        }
        if (!app.containsBean(beanId)) {
            return null;
        }
        return app.getBean(beanId, UIText.class);
    }
}
