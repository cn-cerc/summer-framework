package cn.cerc.db.mysql;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.IDataOperator;
import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.db.core.IHandle;

public class SqlOperator implements IDataOperator {
    private static final Logger log = LoggerFactory.getLogger(SqlOperator.class);

    private String updateKey = "UID_";
    private String tableName;
    private String lastCommand;
    private boolean preview = false;
    private List<String> searchKeys = new ArrayList<>();
    private UpdateMode updateMode = UpdateMode.strict;
    private Connection conntion;

    public SqlOperator(Connection conntion) {
        super();
        this.conntion = conntion;
    }

    public SqlOperator(ISession session) {
        super();
        DataSource dataSource = (DataSource) session.getProperty(MysqlConnection.dataSource);
        try {
            if (dataSource == null) {
                MysqlConnection conn = (MysqlConnection) session.getProperty(MysqlConnection.sessionId);
                conntion = conn.getClient();
            } else {
                conntion = dataSource.getConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public SqlOperator(IHandle owner){
        this(owner.getSession());
    }
    
    // 根据 sql 获取数据库表名
    public static String findTableName(String sql) {
        String result = null;
        String[] items = sql.split("[ \r\n]");
        for (int i = 0; i < items.length; i++) {
            if (items[i].toLowerCase().contains("from")) {
                // 如果取到form后 下一个记录为数据库表名
                while (items[i + 1] == null || "".equals(items[i + 1].trim())) {
                    // 防止取到空值
                    i++;
                }
                result = items[++i]; // 获取数据库表名
                break;
            }
        }

        if (result == null) {
            throw new RuntimeException("sql command error");
        }

        return result;
    }

    private Connection getConnection() {
        return conntion;
    }

    public boolean insert(String tableName, String primaryKey, Record record) {
        this.setTableName(tableName);
        this.setPrimaryKey(primaryKey);
        return this.insert(record);
    }

    @Override
    public boolean insert(Record record) {
        if (record.getFieldDefs().size() == 0) {
            throw new RuntimeException("字段为空");
        }
        Connection conn = getConnection();
        try (BuildStatement bs = new BuildStatement(conn)) {
            if (searchKeys.size() == 0) {
                initPrimaryKeys(conn, record);
            }

            bs.append("insert into ").append(tableName).append(" (");
            int i = 0;
            for (String field : record.getItems().keySet()) {
                if (!updateKey.equals(field)) {
                    i++;
                    if (i > 1) {
                        bs.append(",");
                    }
                    bs.append(field);
                }
            }
            bs.append(") values (");
            i = 0;
            for (String field : record.getItems().keySet()) {
                if (!updateKey.equals(field)) {
                    i++;
                    if (i == 1) {
                        bs.append("?", record.getField(field));
                    } else {
                        bs.append(",?", record.getField(field));
                    }
                }
            }
            bs.append(")");

            PreparedStatement ps = bs.build();
            lastCommand = bs.getCommand();
            if (preview) {
                log.info(lastCommand);
                return false;
            } else {
                log.debug(lastCommand);
            }

            int result = ps.executeUpdate();

            if (searchKeys.contains(updateKey)) {
                BigInteger uidvalue = findAutoUid(conn);
                log.debug("自增列uid value：" + uidvalue);
                record.setField(updateKey, uidvalue);
            }

            return result > 0;
        } catch (SQLException e) {
            log.error(lastCommand);
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean update(Record record) {
        if (!record.isModify()) {
            return false;
        }
        Map<String, Object> delta = record.getDelta();
        if (delta.size() == 0) {
            return false;
        }

        Connection conn = getConnection();
        try (BuildStatement bs = new BuildStatement(conn)) {
            if (this.searchKeys.size() == 0) {
                initPrimaryKeys(conn, record);
            }
            if (searchKeys.size() == 0) {
                throw new RuntimeException("primary keys not exists");
            }
            if (!searchKeys.contains(updateKey)) {
                log.warn(String.format("not find primary key %s in %s", updateKey, this.tableName));
            }
            bs.append("update ").append(tableName);
            // 加入set条件
            int i = 0;
            for (String field : delta.keySet()) {
                if (!updateKey.equals(field)) {
                    i++;
                    bs.append(i == 1 ? " set " : ",");
                    bs.append(field);
                    if (field.indexOf("+") >= 0 || field.indexOf("-") >= 0) {
                        bs.append("?", record.getField(field));
                    } else {
                        bs.append("=?", record.getField(field));
                    }
                }
            }
            if (i == 0) {
                return false;
            }
            // 加入where条件
            i = 0;
            int pkCount = 0;
            for (String field : searchKeys) {
                i++;
                bs.append(i == 1 ? " where " : " and ").append(field);
                Object value = delta.containsKey(field) ? delta.get(field) : record.getField(field);
                if (value != null) {
                    bs.append("=?", value);
                    pkCount++;
                } else {
                    throw new RuntimeException("primaryKey not is null: " + field);
                }
            }
            if (pkCount == 0) {
                throw new RuntimeException("primary keys value not exists");
            }
            if (updateMode == UpdateMode.strict) {
                for (String field : delta.keySet()) {
                    if (!searchKeys.contains(field)) {
                        i++;
                        bs.append(i == 1 ? " where " : " and ").append(field);
                        Object value = delta.get(field);
                        if (value != null) {
                            bs.append("=?", value);
                        } else {
                            bs.append(" is null ");
                        }
                    }
                }
            }

            PreparedStatement ps = bs.build();
            lastCommand = bs.getCommand();
            if (preview) {
                log.info(lastCommand);
                return false;
            }

            if (ps.executeUpdate() != 1) {
                log.error(lastCommand);
                throw new RuntimeException("当前记录已被其它用户修改或不存在，更新失败");
            } else {
                log.debug(lastCommand);
                return true;
            }
        } catch (SQLException e) {
            log.error(lastCommand);
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean delete(Record record) {
        try (BuildStatement bs = new BuildStatement(conntion)) {
            if (this.searchKeys.size() == 0) {
                initPrimaryKeys(conntion, record);
            }
            if (searchKeys.size() == 0) {
                throw new RuntimeException("primary keys  not exists");
            }
            if (!searchKeys.contains(updateKey)) {
                log.warn(String.format("not find primary key %s in %s", updateKey, this.tableName));
            }

            bs.append("delete from ").append(tableName);
            int i = 0;
            Map<String, Object> delta = record.getDelta();
            for (String pk : searchKeys) {
                Object value = delta.containsKey(pk) ? delta.get(pk) : record.getField(pk);
                if (value == null) {
                    throw new RuntimeException("主键值为空");
                }
                i++;
                bs.append(i == 1 ? " where " : " and ");
                bs.append(pk).append("=? ", value);
            }
            PreparedStatement ps = bs.build();
            lastCommand = bs.getCommand();
            if (preview) {
                log.info(lastCommand);
                return false;
            } else {
                log.debug(lastCommand);
            }

            return ps.execute();
        } catch (SQLException e) {
            log.error(lastCommand);
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private void initPrimaryKeys(Connection conn, Record record) throws SQLException {
        for (String key : record.getFieldDefs()) {
            if (updateKey.equalsIgnoreCase(key)) {
                if (!updateKey.equals(key)) {
                    throw new RuntimeException(String.format("%s <> %s", updateKey, key));
                }
                searchKeys.add(updateKey);
                break;
            }
        }
        if (searchKeys.size() == 0) {
            String[] pks = getKeyByDB(conn, tableName).split(";");
            if (pks.length == 0) {
                throw new RuntimeException("获取不到主键PK");
            }
            for (String pk : pks) {
                if (updateKey.equalsIgnoreCase(pk)) {
                    if (!updateKey.equals(pk)) {
                        throw new RuntimeException(String.format("%s <> %s", updateKey, pk));
                    }
                    searchKeys.add(pk);
                    break;
                }
            }
        }
    }

    private BigInteger findAutoUid(Connection conn) {
        BigInteger result = null;
        String sql = "SELECT LAST_INSERT_ID() ";
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                Object obj = rs.getObject(1);
                if (obj instanceof BigInteger) {
                    result = (BigInteger) obj;
                } else {
                    result = BigInteger.valueOf(rs.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }
        if (result == null) {
            throw new RuntimeException("未获取UID");
        }
        return result;
    }

    private String getKeyByDB(Connection conn, String tableName) throws SQLException {
        StringBuffer result = new StringBuffer();
        try (BuildStatement bs = new BuildStatement(conn)) {
            bs.append("select COLUMN_NAME from INFORMATION_SCHEMA.COLUMNS ");
            bs.append("where table_name= ? AND COLUMN_KEY= 'PRI' ", tableName);
            PreparedStatement ps = bs.build();
            log.debug(ps.toString().split(":")[1].trim());
            ResultSet rs = ps.executeQuery();
            int i = 0;
            while (rs.next()) {
                i++;
                if (i > 1) {
                    result.append(";");
                }
                result.append(rs.getString("COLUMN_NAME"));
            }
            return result.toString();
        }
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getLastCommand() {
        return lastCommand;
    }

    public boolean isPreview() {
        return preview;
    }

    public void setPreview(boolean preview) {
        this.preview = preview;
    }

    @Deprecated // 请改使用 getSearchKeys
    public List<String> getPrimaryKeys() {
        return searchKeys;
    }

    public List<String> getSearchKeys() {
        return searchKeys;
    }

    public UpdateMode getUpdateMode() {
        return updateMode;
    }

    public void setUpdateMode(UpdateMode updateMode) {
        this.updateMode = updateMode;
    }

    @Deprecated // 请改使用 getUpdateKey
    public String getPrimaryKey() {
        return updateKey;
    }

    @Deprecated // 请改使用 setUpdateKey
    public void setPrimaryKey(String primaryKey) {
        this.updateKey = primaryKey;
    }

    public String getUpdateKey() {
        return updateKey;
    }

    public void setUpdateKey(String updateKey) {
        this.updateKey = updateKey;
    }

}
