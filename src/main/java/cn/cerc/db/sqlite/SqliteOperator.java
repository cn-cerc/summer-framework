package cn.cerc.db.sqlite;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.FieldMeta.FieldType;
import cn.cerc.core.Record;
import cn.cerc.db.core.SqlOperator;
import cn.cerc.db.mysql.BuildStatement;
import cn.cerc.db.mysql.UpdateMode;

public class SqliteOperator extends SqlOperator {
    private static final Logger log = LoggerFactory.getLogger(SqliteOperator.class);
    private String lastCommand;

    public SqliteOperator() {
        super();
        this.setUpdateKey("id");
    }

    @Override
    public boolean insert(Connection connection, Record record) {
        List<String> fields = record.getFieldDefs().getFields(FieldType.Storage);
        if (fields.size() == 0)
            throw new RuntimeException("storage field is empty");

        try (BuildStatement bs = new BuildStatement(connection)) {
            if (searchKeys.size() == 0) {
                initPrimaryKeys(connection, record);
            }

            bs.append("insert into ").append(getTableName()).append(" (");
            int i = 0;
            for (String field : record.getItems().keySet()) {
                if (!getUpdateKey().equals(field)) {
                    if (fields.contains(field)) {
                        i++;
                        if (i > 1) {
                            bs.append(",");
                        }
                        bs.append(field);
                    }
                }
            }
            bs.append(") values (");
            i = 0;
            for (String field : record.getItems().keySet()) {
                if (!getUpdateKey().equals(field)) {
                    if (fields.contains(field)) {
                        i++;
                        if (i == 1) {
                            bs.append("?", record.getField(field));
                        } else {
                            bs.append(",?", record.getField(field));
                        }
                    }
                }
            }
            bs.append(")");

            PreparedStatement ps = bs.build();
            lastCommand = bs.getCommand();
            if (isDebug()) {
                log.info(lastCommand);
                return false;
            } else {
                log.debug(lastCommand);
            }

            int result = ps.executeUpdate();

            if (searchKeys.contains(getUpdateKey())) {
                BigInteger uidvalue = findAutoUid(connection);
                log.debug("自增列uid value：" + uidvalue);

                if (uidvalue.intValue() <= Integer.MAX_VALUE) {
                    record.setField(getUpdateKey(), uidvalue.intValue());
                } else {
                    record.setField(getUpdateKey(), uidvalue);
                }
            }

            return result > 0;
        } catch (SQLException e) {
            log.error(lastCommand);
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean update(Connection connection, Record record) {
        if (!record.isModify()) {
            return false;
        }
        Map<String, Object> delta = record.getDelta();
        if (delta.size() == 0) {
            return false;
        }
        List<String> fields = record.getFieldDefs().getFields(FieldType.Storage);
        if (fields.size() == 0)
            throw new RuntimeException("storage field is empty");

        try (BuildStatement bs = new BuildStatement(connection)) {
            if (this.searchKeys.size() == 0) {
                initPrimaryKeys(connection, record);
            }
            if (searchKeys.size() == 0) {
                throw new RuntimeException("primary keys not exists");
            }
            if (!searchKeys.contains(getUpdateKey())) {
                log.warn(String.format("not find primary key %s in %s", getUpdateKey(), getTableName()));
            }
            bs.append("update ").append(getTableName());
            // 加入set条件
            int i = 0;
            for (String field : delta.keySet()) {
                if (!getUpdateKey().equals(field)) {
                    if (fields.contains(field)) {
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
            }
            if (i == 0) {
                return false;
            }
            // 加入where条件
            i = 0;
            int pkCount = 0;
            for (String field : searchKeys) {
                if (fields.contains(field)) {
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
            }
            if (pkCount == 0) {
                throw new RuntimeException("primary keys value not exists");
            }
            if (getUpdateMode() == UpdateMode.strict) {
                for (String field : delta.keySet()) {
                    if (!searchKeys.contains(field)) {
                        if (fields.contains(field)) {
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
            }

            PreparedStatement ps = bs.build();
            lastCommand = bs.getCommand();
            if (isDebug()) {
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
    public boolean delete(Connection connection, Record record) {
        List<String> fields = record.getFieldDefs().getFields(FieldType.Storage);
        if (fields.size() == 0)
            throw new RuntimeException("storage field is empty");

        try (BuildStatement bs = new BuildStatement(connection)) {
            if (this.searchKeys.size() == 0) {
                initPrimaryKeys(connection, record);
            }
            if (searchKeys.size() == 0) {
                throw new RuntimeException("primary keys not exists");
            }
            if (!searchKeys.contains(getUpdateKey())) {
                log.warn(String.format("not find primary key %s in %s", getUpdateKey(), getTableName()));
            }

            bs.append("delete from ").append(getTableName());
            int i = 0;
            Map<String, Object> delta = record.getDelta();
            for (String field : searchKeys) {
                Object value = delta.containsKey(field) ? delta.get(field) : record.getField(field);
                if (value == null) {
                    throw new RuntimeException("primary key is null");
                }
                if (fields.contains(field)) {
                    i++;
                    bs.append(i == 1 ? " where " : " and ");
                    bs.append(field).append("=? ", value);
                }
            }
            PreparedStatement ps = bs.build();
            lastCommand = bs.getCommand();
            if (isDebug()) {
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
        for (String key : record.getFieldDefs().getFields()) {
            if (getUpdateKey().equalsIgnoreCase(key)) {
                if (!getUpdateKey().equals(key)) {
                    throw new RuntimeException(String.format("%s <> %s", getUpdateKey(), key));
                }
                searchKeys.add(getUpdateKey());
                break;
            }
        }
    }

    private BigInteger findAutoUid(Connection conn) {
        BigInteger result = null;
        String sql = "select last_insert_rowid() newid";
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

    public String getLastCommand() {
        return lastCommand;
    }

}
