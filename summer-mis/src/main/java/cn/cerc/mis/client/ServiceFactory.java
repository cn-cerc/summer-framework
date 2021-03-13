package cn.cerc.mis.client;

import cn.cerc.db.core.IHandle;
import cn.cerc.db.cache.Buffer;
import cn.cerc.db.mysql.MysqlConnection;
import cn.cerc.mis.config.ApplicationConfig;
import cn.cerc.mis.core.BookHandle;
import cn.cerc.mis.core.LocalService;

public class ServiceFactory {

    public static final String BOOK_PUBLIC = "public"; // 数据库中心

    public static IServiceProxy get(IHandle handle) {
        IServiceProxy svr = ServiceFactory.get(handle, ServiceFactory.BOOK_PUBLIC);
        return svr;
    }

    public static IServiceProxy get(IHandle handle, String corpNo) {
        if ("public".equals(corpNo)) {
            if (ApplicationConfig.isMaster()) {
                return new LocalService(handle);
            } else {
                return RemoteService.create(handle, corpNo);
            }
        }

        if (corpNo.equals(handle.getCorpNo())) {
            return new LocalService(handle);
        } else {
            MysqlConnection curConn = (MysqlConnection) handle.getProperty(MysqlConnection.sessionId);
            String curDB = curConn.getDatabase();

            Buffer buff = new Buffer(ServiceFactory.class.getName(), corpNo);
            String tarDB = buff.getString("database");
            if (tarDB == null || "".equals(tarDB)) {
                RemoteService svr = RemoteService.create(handle, ServiceFactory.BOOK_PUBLIC);
                svr.setService("ApiDB.getDatabase");
                if (!svr.exec("bookNo", corpNo)) {
                    throw new RuntimeException(svr.getMessage());
                }
                tarDB = svr.getDataOut().getHead().getString("database");
                buff.setField("database", tarDB);
                buff.post();
            }

            if (tarDB.equals(curDB)) {
                return new LocalService(new BookHandle(handle, corpNo));
            } else {
                return RemoteService.create(handle, corpNo);
            }
        }
    }

}
