package cn.cerc.db.core;

import java.sql.Connection;

public interface ConnectionClient extends AutoCloseable {

    Connection getConnection();

}
