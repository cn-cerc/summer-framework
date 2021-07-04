package cn.cerc.db.dao;

import java.lang.annotation.Annotation;
import java.math.BigInteger;
import java.sql.Timestamp;

import javax.persistence.Entity;

import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.core.RecordUtils;
import cn.cerc.db.core.Handle;
import cn.cerc.db.mysql.MysqlQuery;

public class DaoUtil {
    // 将obj的数据，复制到record中
    @Deprecated
    public static void copy(Object obj, Record record) {
        RecordUtils.copyToRecord(obj, record);
    }

    // 将record的数据，复制到obj中
    @Deprecated
    public static void copy(Record record, Object obj) {
        RecordUtils.copyToObject(record, obj);
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

        MysqlQuery ds = new MysqlQuery(new Handle(session));
        ds.add("select * from " + tableName);
        ds.getSqlText().setMaximum(1);
        ds.open();
        Record record = ds.eof() ? null : ds.getCurrent();
        for (String field : ds.getFieldDefs().getFields()) {
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
