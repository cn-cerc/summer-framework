package cn.cerc.mis.client;

import cn.cerc.db.core.IHandle;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.IServiceProxyFactory;

public class ServiceFactory {
    private static IServiceProxyFactory factory;

    public static final String BOOK_PUBLIC = "public"; // 数据库中心

    public static IServiceProxy get(IHandle handle) {
        return get(handle, BOOK_PUBLIC);
    }

    @Deprecated
    public static IServiceProxy get(IHandle handle, String corpNo) {
        if (factory == null)
            factory = Application.getBeanDefault(IServiceProxyFactory.class, handle.getSession());
        return factory.get(handle, corpNo);
    }

}
