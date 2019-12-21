package cn.cerc.mis.tools;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import cn.cerc.ui.core.Component;
import cn.cerc.ui.parts.UISheetUrl;

public class PageLink {
    private static ApplicationContext app;
    private static String xmlFile = "classpath:page-link.xml";

    public static UISheetUrl get(Component owner, String beanId) {
        if (app == null)
            app = new FileSystemXmlApplicationContext(xmlFile);
        if (!app.containsBean(beanId))
            return null;
        UISheetUrl side = app.getBean(beanId, UISheetUrl.class);
        side.setOwner(owner);
        return side;
    }

    public static void main(String[] args) {
        UISheetUrl help = get(null, "TFrmPartBrand");
        System.out.println(help.toString());
    }
}
