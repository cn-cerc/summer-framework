package cn.cerc.db.core;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import cn.cerc.core.Record;
import cn.cerc.db.mysql.UpdateMode;

public abstract class SqlOperator {
    private String tableName;
    private String updateKey;
    private UpdateMode updateMode = UpdateMode.strict;
    protected List<String> searchKeys = new ArrayList<>();
    private boolean debug = false;

    public final String getTableName() {
        return tableName;
    }

    public final void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public final String getUpdateKey() {
        return updateKey;
    }

    public final void setUpdateKey(String updateKey) {
        this.updateKey = updateKey;
    }

    public final UpdateMode getUpdateMode() {
        return updateMode;
    }

    public final void setUpdateMode(UpdateMode updateMode) {
        this.updateMode = updateMode;
    }

    public final boolean isDebug() {
        return debug;
    }

    public final void setDeubg(boolean debug) {
        this.debug = debug;
    }

    @Deprecated // 请改使用 getSearchKeys
    public final List<String> getPrimaryKeys() {
        return searchKeys;
    }

    public final List<String> getSearchKeys() {
        return searchKeys;
    }

    @Deprecated // 请改使用 getUpdateKey
    public final String getPrimaryKey() {
        return getUpdateKey();
    }

    @Deprecated // 请改使用 setUpdateKey
    public final void setPrimaryKey(String primaryKey) {
        this.setUpdateKey(primaryKey);
    }

    public abstract boolean insert(Connection connection, Record record);

    public abstract boolean update(Connection connection, Record record);

    public abstract boolean delete(Connection connection, Record record);

}
