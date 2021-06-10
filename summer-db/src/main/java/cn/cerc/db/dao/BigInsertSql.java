package cn.cerc.db.dao;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ClassData;
import cn.cerc.core.ClassFactory;
import cn.cerc.db.mysql.BuildStatement;

public class BigInsertSql {
    private static final Logger log = LoggerFactory.getLogger(BigInsertSql.class);

    public static boolean exec(Connection connection, Object oldRecord, boolean preview) {
        String lastCommand = null;
        ClassData classData = ClassFactory.get(oldRecord.getClass());
        String updateKey = classData.getUpdateKey();
        try (BuildStatement bs = new BuildStatement(connection)) {
            Map<String, Object> items = new LinkedHashMap<>();
            BigOperator.copy(oldRecord, (key, value) -> {
                items.put(key, value);
            });

            bs.append("insert into ").append(classData.getTableId()).append(" (");
            int i = 0;
            for (String field : items.keySet()) {
                if (!updateKey.equals(field)) {
                    i++;
                    if (i <= 1) {
                    } else {
                        bs.append(",");
                    }
                    bs.append(field);
                }
            }
            bs.append(") values (");

            // 预编译sql
            i = 0;
            for (String field : items.keySet()) {
                if (!updateKey.equals(field)) {
                    i++;
                    bs.append(i == 1 ? "?" : ",?", items.get(field));
                }
            }
            bs.append(")");

            PreparedStatement ps = bs.build();
            lastCommand = bs.getCommand();
            log.debug(lastCommand);
            if (preview) {
                return false;
            }

            int result = ps.executeUpdate();

            BigInteger uidvalue = findAutoUid(connection);
            if (uidvalue == null) {
                throw new RuntimeException("未获取:" + updateKey);
            }

            log.debug("auto increment uid value：" + uidvalue);
            setAutoUid(oldRecord, uidvalue.longValue());
            return result > 0;
        } catch (SQLException e) {
            log.error(lastCommand);
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private static BigInteger findAutoUid(Connection conn) {
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
        return result;
    }

    private static void setAutoUid(Object object, long value) {
        for (Field field : object.getClass().getDeclaredFields()) {
            Column column = null;
            boolean isUid = false;
            for (Annotation item : field.getAnnotations()) {
                if (item instanceof Column) {
                    column = (Column) item;
                    break;
                }
            }
            for (Annotation item : field.getAnnotations()) {
                if (item instanceof GeneratedValue) {
                    if (((GeneratedValue) item).strategy() == GenerationType.IDENTITY) {
                        isUid = true;
                    }
                    break;
                }
            }
            if (column != null && isUid) {
                try {
                    field.setAccessible(true);
                    field.setLong(object, value);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
