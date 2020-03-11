package cn.cerc.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClassData {
    public static final int PUBLIC = 1;
    public static final int PRIVATE = 2;
    public static final int PROTECTED = 4;
    private Class<?> clazz;
    private String tableId = null;
    private String select = null;
    private Map<String, Field> fields = null;
    private Field generationIdentityField = null;
    private String updateKey = "UID_";
    private List<String> searchKeys = new ArrayList<>();
    private List<String> specialNumKeys = new ArrayList<>();

    public ClassData(Class<?> clazz) {
        this.clazz = clazz;
        for (Annotation anno : clazz.getAnnotations()) {
            if (anno instanceof Entity) {
                Entity entity = (Entity) anno;
                if (!"".equals(entity.name())) {
                    tableId = entity.name();
                }
            }
            if (anno instanceof Select) {
                Select obj = (Select) anno;
                if (!"".equals(obj.value())) {
                    select = obj.value();
                }
            }
        }

        this.fields = loadFields();

        if (tableId == null && select == null) {
            throw new RuntimeException("entity.name or select not define");
        }

        if (select == null) {
            StringBuffer sb = new StringBuffer();
            sb.append("select ");
            int i = 0;
            for (String key : fields.keySet()) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append("`" + key + "`");
                i++;
            }
            sb.append(" from ").append("`" + tableId + "`");
            select = sb.toString();
        } else if (tableId == null) {
            String[] items = select.split("[ \r\n]");
            for (int i = 0; i < items.length; i++) {
                if (items[i].toLowerCase().contains("from")) {
                    // 如果取到form后 下一个记录为数据库表名
                    while (items[i + 1] == null || "".equals(items[i + 1].trim())) {
                        // 防止取到空值
                        i++;
                    }
                    tableId = items[++i]; // 获取数据库表名
                    break;
                }
            }

            if (tableId == null) {
                throw new RuntimeException("entity.name or select not define");
            }
        }

        // 查找自增字段并赋值
        int count = 0;
        for (String key : fields.keySet()) {
            Field field = fields.get(key);
            for (Annotation item : field.getAnnotations()) {
                if (item instanceof GeneratedValue) {
                    if (((GeneratedValue) item).strategy() == GenerationType.IDENTITY) {
                        generationIdentityField = field;
                        count++;
                    }
                }
                if (item instanceof Id) {
                    updateKey = key;
                }
                if (item instanceof SearchKey) {
                    searchKeys.add(key);
                }
                if (item instanceof SpecialNum) {
                    specialNumKeys.add(key);
                }
            }
        }

        if (count > 1) {
            throw new RuntimeException("support one generationIdentityField!");
        }

        if (searchKeys.size() == 0) {
            searchKeys.add(updateKey);
        }
    }

    private Map<String, Field> loadFields() {
        Map<String, Field> fields = new LinkedHashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            Column column = null;
            for (Annotation item : field.getAnnotations()) {
                if (item instanceof Column) {
                    column = (Column) item;
                    break;
                }
            }
            if (column != null) {
                String fieldCode = field.getName();
                if (!"".equals(column.name())) {
                    fieldCode = column.name();
                }

                if (field.getModifiers() == Modifier.PUBLIC) {
                    fields.put(fieldCode, field);
                } else if (field.getModifiers() == Modifier.PRIVATE || field.getModifiers() == Modifier.PROTECTED) {
                    field.setAccessible(true);
                    fields.put(fieldCode, field);
                }
            }
        }
        return fields;
    }

    public List<String> getSearchKeys() {
        return searchKeys;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getTableId() {
        return tableId;
    }

    public String getSelect() {
        return select;
    }

    public Map<String, Field> getFields() {
        return fields;
    }

    public Field getGenerationIdentityField() {
        return generationIdentityField;
    }

    public String getUpdateKey() {
        return updateKey;
    }

    public List<String> getSpecialNumKeys() {
        return specialNumKeys;
    }
}
