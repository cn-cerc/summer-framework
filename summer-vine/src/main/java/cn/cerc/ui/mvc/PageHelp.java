package cn.cerc.ui.mvc;

import cn.cerc.ui.core.Component;
import cn.cerc.ui.parts.UISheetHelp;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class PageHelp {
    private static ApplicationContext app;
    private static String xmlFile = "classpath:page-help.xml";

    public static UISheetHelp get(Component owner, String beanId) {
        if (app == null) {
            app = new FileSystemXmlApplicationContext(xmlFile);
        }
        if (!app.containsBean(beanId)) {
            return null;
        }
        UISheetHelp side = app.getBean(beanId, UISheetHelp.class);
        side.setOwner(owner);
        return side;
    }

    public static void main(String[] args) {
        UISheetHelp help = get(null, "TFrmTranBG");
        System.out.println(help.toString());
    }
}
