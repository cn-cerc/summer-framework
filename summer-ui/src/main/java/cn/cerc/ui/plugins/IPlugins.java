package cn.cerc.ui.plugins;

/**
 * 为系统提供客制化支持
 *
 */
public interface IPlugins {

    void setOwner(Object owner);

    default String getFuncCode() {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
//        for (StackTraceElement item : stacktrace) {
//            System.out.println(item.getMethodName());
//        }
        StackTraceElement e = stacktrace[3];
        return e.getMethodName();
    }
}
