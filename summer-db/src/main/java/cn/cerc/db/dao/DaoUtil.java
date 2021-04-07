package cn.cerc.db.dao;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.core.TDateTime;
import cn.cerc.db.mysql.SqlQuery;

public class DaoUtil {
    private static final Logger log = LoggerFactory.getLogger(DaoUtil.class);

    private static int PUBLIC = 1;
    private static int PRIVATE = 2;
    private static int PROTECTED = 4;

    // 将obj的数据，复制到record中
    public static void copy(Object obj, Record record) {
        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                Column column = findColumn(field.getAnnotations());
                if (column != null) {
                    String fieldName = field.getName();
                    if (!"".equals(column.name())) {
                        fieldName = column.name();
                    }
                    if (field.getModifiers() == PUBLIC) {
                        record.setField(fieldName, field.get(obj));
                    } else if (field.getModifiers() == PRIVATE || field.getModifiers() == PROTECTED) {
                        field.setAccessible(true);
                        record.setField(fieldName, field.get(obj));
                    }
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    // 将record的数据，复制到obj中
    public static void copy(Record record, Object obj) {
        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            // 找出所有的数据字段
            Map<Field, Column> items = new HashMap<>();
            for (Field field : fields) {
                Column column = findColumn(field.getAnnotations());
                if (column != null) {
                    if (field.getModifiers() == PUBLIC) {
                        items.put(field, column);
                    } else if (field.getModifiers() == PRIVATE || field.getModifiers() == PROTECTED) {
                        field.setAccessible(true);
                        items.put(field, column);
                    }
                }
            }

            if (record.getFieldDefs().size() != items.size()) {
                if (record.getFieldDefs().size() > items.size()) {
                    log.warn("field[].size > property[].size");
                } else {
                    throw new RuntimeException(String.format("field[].size %d < property[].size %d",
                            record.getFieldDefs().size(), items.size()));
                }
            }

            // 查找并赋值
            for (String fieldName : record.getFieldDefs()) {
                boolean exists = false;
                for (Field field : items.keySet()) {
                    // 默认等于对象的属性
                    String propertyName = field.getName();
                    Column column = items.get(field);
                    if (!"".equals(column.name())) {
                        propertyName = column.name();
                    }
                    if (propertyName.equals(fieldName)) {
                        Object val = record.getField(fieldName);
                        if (val == null) {
                            field.set(obj, null);
                        } else if (field.getType().equals(val.getClass())) {
                            field.set(obj, val);
                        } else {
                            if ("int".equals(field.getType().getName())) {
                                field.setInt(obj, (Integer) val);
                            } else if ("double".equals(field.getType().getName())) {
                                field.setDouble(obj, (Double) val);
                            } else if ("long".equals(field.getType().getName())) {
                                if (val instanceof BigInteger) {
                                    field.setLong(obj, ((BigInteger) val).longValue());
                                } else {
                                    field.setLong(obj, (Long) val);
                                }
                            } else if ("boolean".equals(field.getType().getName())) {
                                field.setBoolean(obj, (Boolean) val);
                            } else if (TDateTime.class.getName().equals(field.getType().getName())) {
                                field.set(obj, new TDateTime((Date) val));
                            } else {
                                throw new RuntimeException("error: " + field.getType().getName() + " as " + val.getClass().getName());
                            }
                        }
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    log.warn("property not find: " + fieldName);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static Column findColumn(Annotation[] annotations) {
        Column column = null;
        for (Annotation item : annotations) {
            if (item instanceof Column) {
                column = (Column) item;
                break;
            }
        }
        return column;
    }

    // 根据实体对象，取出数据表名
    public static String getTableName(Class<?> clazz) {
        String result = null;
        for (Annotation anno : clazz.getAnnotations()) {
            if (anno instanceof Entity) {
                Entity entity = (Entity) anno;
                if (!"".equals(entity.name())) {
                    result = entity.name();
                }
                break;
            }
        }
        if (result == null) {
            throw new RuntimeException("tableName not define");
        }
        return result;
    }

    // 根据数据表名，得到实体类
    public static String buildEntity(ISession session, String tableName, String className) {
        StringBuffer sb = new StringBuffer();
        sb.append("import javax.persistence.Entity;\r\n");
        sb.append(String.format("@Entity(name = \"%s\")", tableName)).append("\r\n");
        sb.append("public class " + className + "{").append("\r\n");
        sb.append("\r\n");

        SqlQuery ds = new SqlQuery(session);
        ds.add("select * from " + tableName);
        ds.getSqlText().setMaximum(1);
        ds.open();
        Record record = ds.eof() ? null : ds.getCurrent();
        for (String field : ds.getFieldDefs()) {
            if ("UID_".equals(field)) {
                sb.append("@Id").append("\r\n");
                sb.append("@GeneratedValue(strategy = GenerationType.IDENTITY)").append("\r\n");
            }
            sb.append(String.format("@Column(name=\"%s\")", field)).append("\r\n");
            String remark = null;
            sb.append("private ");
            if (record != null) {
                Object val = record.getField(field);
                if (val != null) {
                    // Class<?> clazz = val.getClass();
                    if (val instanceof Integer) {
                        sb.append("int");
                    } else if (val instanceof BigInteger) {
                        sb.append("long");
                    } else if (val instanceof Boolean) {
                        sb.append("boolean");
                    } else if (val instanceof String) {
                        sb.append("String");
                    } else if (val instanceof Double) {
                        sb.append("double");
                    } else if (val instanceof Long) {
                        sb.append("long");
                    } else if (val instanceof Timestamp) {
                        sb.append("TDateTime");
                    } else {
                        remark = val.getClass().getName();
                        sb.append("String");
                    }
                } else {
                    sb.append("String");
                }
            } else {
                sb.append("String");
            }
            sb.append(" ");
            if ("UID_".equals(field)) {
                sb.append("uid");
            } else if ("ID_".equals(field)) {
                sb.append("id");
            } else {
                sb.append(field.substring(0, 1).toLowerCase());
                if (field.endsWith("_")) {
                    sb.append(field, 1, field.length() - 1);
                } else {
                    sb.append(field.substring(1));
                }
            }
            sb.append(";");
            if (remark != null) {
                sb.append("//").append(remark);
            }
            sb.append("\r\n");
            sb.append("\r\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
