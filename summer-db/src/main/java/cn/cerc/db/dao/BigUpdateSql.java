package cn.cerc.db.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ClassData;
import cn.cerc.core.ClassFactory;
import cn.cerc.core.ClassResource;
import cn.cerc.db.SummerDB;
import cn.cerc.db.mysql.BuildStatement;
import cn.cerc.db.mysql.UpdateMode;

public class BigUpdateSql {
    private static final ClassResource res = new ClassResource(BigUpdateSql.class, SummerDB.ID);
    private static final Logger log = LoggerFactory.getLogger(BigUpdateSql.class);

    public static boolean exec(Connection conn, Object oldRecord, Object curRecord, UpdateMode updateMode,
            boolean preview) throws Exception {
        String lastCommand = null;
        ClassData classData = ClassFactory.get(curRecord.getClass());
        String updateKey = classData.getUpdateKey();
        Map<String, Object> items = new LinkedHashMap<>();
        Map<String, Object> delta = new LinkedHashMap<>();
        BigOperator.copy(oldRecord, (key, value) -> {
            items.put(key, value);
        });

        BigOperator.copy(curRecord, (key, value) -> {
            Object old = items.get(key);
            if (old != null && !old.equals(value)) {
                delta.put(key, value);
            } else if (old == null && value != null) {
                delta.put(key, value);
            }
        });
        if (delta.size() == 0) {
            return false;
        }

        try (BuildStatement bs = new BuildStatement(conn)) {
            bs.append("update ").append(classData.getTableId());
            // 加入set条件
            int i = 0;
            List<String> specialNumKeys = classData.getSpecialNumKeys();
            for (String field : delta.keySet()) {
                if (!updateKey.equals(field)) {
                    i++;
                    bs.append(i == 1 ? " set " : ",");
                    if (specialNumKeys.contains(field)) {
                        Object oldValue = items.get(field);
                        Object newValue = delta.get(field);
                        Object value = null;
                        if (newValue instanceof BigRecord && oldValue instanceof BigRecord) {
                            value = ((BigRecord) newValue).getDiffValue(field, (BigRecord) oldValue);
                            if (value == null)
                                throw new RuntimeException("getDiffValue is null");
                        } else {
                            String typeName = classData.getFields().get(field).getType().getName();
                            value = getDiffValue(typeName, oldValue, newValue);
                        }
                        bs.append(String.format("`%s`=`%s`+(?)", field, field), value);
                    } else {
                        bs.append(String.format("`%s`=?", field), delta.get(field));
                    }
                }
            }
            if (i == 0) {
                return false;
            }
            // 加入where条件
            i = 0;
            i++;
            bs.append(i == 1 ? " where " : " and ").append(updateKey);
            Object value = delta.containsKey(updateKey) ? delta.get(updateKey) : items.get(updateKey);
            if (value == null) {
                throw new RuntimeException("primaryKey not is null: " + updateKey);
            }
            bs.append("=?", value);

            if (updateMode == UpdateMode.strict) {
                for (String field : delta.keySet()) {
                    if (!updateKey.contains(field)) {
                        bs.append(" and ").append(field);
                        Object obj = items.get(field);
                        if (obj != null) {
                            bs.append("=?", obj);
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
                throw new RuntimeException(res.getString(1, "当前记录已被其它用户修改或不存在，更新失败"));
            } else {
                log.debug(lastCommand);
                return true;
            }
        } catch (Exception e) {
            log.error(lastCommand);
            e.printStackTrace();
            throw e;
        }
    }

    public static Object getDiffValue(String fieldTypeName, Object oldValue, Object newValue)
            throws IllegalAccessException {
        Object value = null;
        String[] items = fieldTypeName.split("\\.");
        if (items.length == 0) {
            throw new RuntimeException("fieldTypeName error");
        }
        String typeName = items[items.length - 1];
        if ("short,Short".indexOf(typeName) > -1) {
            short n1 = oldValue != null ? (Short) oldValue : 0;
            short n2 = newValue != null ? (Short) newValue : 0;
            value = n2 - n1;
        } else if ("int,Integer".indexOf(typeName) > -1) {
            int n1 = oldValue != null ? (Integer) oldValue : 0;
            int n2 = newValue != null ? (Integer) newValue : 0;
            value = n2 - n1;
        } else if ("long,Long".indexOf(typeName) > -1) {
            long n1 = oldValue != null ? (Long) oldValue : 0;
            long n2 = newValue != null ? (Long) newValue : 0;
            value = n2 - n1;
        } else if ("float,Float".indexOf(typeName) > -1) {
            float n1 = oldValue != null ? (Float) oldValue : 0;
            float n2 = newValue != null ? (Float) newValue : 0;
            value = n2 - n1;
        } else if ("double,Double".indexOf(typeName) > -1) {
            double n1 = oldValue != null ? (Double) oldValue : 0;
            double n2 = newValue != null ? (Double) newValue : 0;
            value = n2 - n1;
        } else if ("BigInteger".indexOf(typeName) > -1) {
            BigInteger n1 = (BigInteger) oldValue;
            BigInteger n2 = (BigInteger) newValue;
            value = n2.subtract(n1);
        } else if ("BigDecimal".indexOf(typeName) > -1) {
            BigDecimal n1 = (BigDecimal) oldValue;
            BigDecimal n2 = (BigDecimal) newValue;
            value = n2.subtract(n1);
        } else {
            throw new RuntimeException(String.format(res.getString(2, "不支持的数据类型：%s"), typeName));
        }
        return value;
    }
}
