package cn.cerc.db.dao;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ClassData;
import cn.cerc.core.ClassFactory;
import cn.cerc.core.TDate;
import cn.cerc.core.TDateTime;

public class BigOperator {
    private static final Logger log = LoggerFactory.getLogger(BigOperator.class);

    public static void copy(Object object, ReadFieldEvent event) {
        if (object == null) {
            log.error("object is null");
            return;
        }
        ClassData classData = ClassFactory.get(object.getClass());
        try {
            for (String fieldCode : classData.getFields().keySet()) {
                Field field = classData.getFields().get(fieldCode);
                event.put(fieldCode, field.get(object));
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        // for (String key : items.keySet()) {
        // Object value = items.get(key);
        // System.out.println(String.format("key:%s, value: %s", key, value == null ?
        // "null" : value.toString()));
        // }
    }

    public static void copy(Map<String, Object> items, Object obj) {
        String msg = null;
        try {
            // 找出所有的数据字段
            ClassData classData = ClassFactory.get(obj.getClass());
            Map<String, Field> fields = classData.getFields();
            // 查找并赋值
            for (String fieldCode : items.keySet()) {
                Field field = fields.get(fieldCode);
                if (field != null) {
                    Object val = items.get(fieldCode);
                    if (val == null) {
                        if ("int,double,long".indexOf(field.getType().getName()) > -1) {
                            field.set(obj, 0);
                        } else if ("boolean".indexOf(field.getType().getName()) > -1) {
                            field.set(obj, false);
                        } else {
                            field.set(obj, null);
                        }
                    } else if (field.getType().equals(val.getClass())) {
                        field.set(obj, val);
                    } else {
                        msg = field.getName();
                        if ("int".equals(field.getType().getName())) {
                            if (val instanceof Long) {
                                field.setInt(obj, ((Long) val).intValue());
                            } else {
                                field.setInt(obj, (Integer) val);
                            }
                        } else if ("double".equals(field.getType().getName())) {
                            if (val instanceof BigDecimal) {
                                field.setDouble(obj, ((BigDecimal) val).doubleValue());
                            } else {
                                field.setDouble(obj, (Double) val);
                            }
                        } else if ("long".equals(field.getType().getName())) {
                            if (val instanceof BigInteger) {
                                field.setLong(obj, ((BigInteger) val).longValue());
                            } else if (val instanceof Integer) {
                                field.setLong(obj, (Integer) val);
                            } else {
                                field.setLong(obj, (Long) val);
                            }
                        } else if ("boolean".equals(field.getType().getName())) {
                            field.setBoolean(obj, (Boolean) val);
                        } else if (TDateTime.class.getName().equals(field.getType().getName())) {
                            if (val instanceof TDate) {
                                field.set(obj, new TDateTime(((TDate) val).getData()));
                            } else if (val instanceof TDateTime) {
                                field.set(obj, new TDateTime(((TDateTime) val).getData()));
                            } else {
                                field.set(obj, new TDateTime((Date) val));
                            }
                        } else {
                            throw new RuntimeException(String.format("error: fieldCode-%s, value-%s %s as %s",
                                    fieldCode, val, field.getType().getName(), val.getClass().getName()));
                        }
                    }
                } else {
                    log.warn("property not find: " + fieldCode);
                }
            }
        } catch (IllegalAccessException | ClassCastException e) {
            if (msg != null) {
                log.error("field: " + msg);
            }
            e.printStackTrace();
        }
    }

}
