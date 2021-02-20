package cn.cerc.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FieldDefs implements Serializable, Iterable<String> {
    private static final long serialVersionUID = 7478897050846245325L;
    private List<String> fields = new ArrayList<>();
    // 设置字段为强类型，必须预先定义，默认为弱类型
    private boolean strict = false;
    // 设置是否不再允许添加字段，默认为可随时添加
    private boolean locked = false;

    public boolean exists(String field) {
        return fields.contains(field);
    }

    @Override
    public String toString() {
        return "TFieldDefs [fields=" + fields + "]";
    }

    public List<String> getFields() {
        return fields;
    }

    public FieldDefs add(String field) {
        if (this.locked) {
            throw new RuntimeException("locked is true");
        }
        if (this.strict) {
            throw new RuntimeException("strict is true");
        }
        if (field == null || "".equals(field)) {
            throw new RuntimeException("field is null!");
        }
        if (!fields.contains(field)) {
            fields.add(field);
        }
        return this;
    }

    public void add(String... strs) {
        for (String field : strs) {
            this.add(field);
        }
    }

    public void clear() {
        fields.clear();
    }

    public int size() {
        return fields.size();
    }

    @Override
    public Iterator<String> iterator() {
        return this.getFields().iterator();
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void delete(String field) {
        int index = fields.indexOf(field);
        if (index != -1) {
            fields.remove(index);
        }
    }
}
