package cn.cerc.mis.vcl;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

/**
 * 用于建立客户端窗口
 *
 * @author 张弓
 */
public class TApplication {
    private TCustomForm mainForm;

    public TCustomForm createForm(Class<?> clazz) {
        try {
            mainForm = (TCustomForm) clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        return mainForm;
    }

    public void run() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                mainForm.setVisible(true);
            }
        });
    }

    public TCustomForm getMainForm() {
        return mainForm;
    }

    public void setMainForm(TCustomForm mainForm) {
        this.mainForm = mainForm;
    }

    public static void main(String[] args) {
        TApplication app = new TApplication();
        app.createForm(TCustomForm.class);
        app.run();
    }

}
