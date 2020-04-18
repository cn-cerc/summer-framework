package cn.cerc.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchDataSet {
    private DataSet dataSet;
    private Map<String, Record> items;
    private List<String> fields = new ArrayList<>();
    private String keyFields;

    public SearchDataSet() {

    }

    public SearchDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    public SearchDataSet add(DataSet dataSet) {
        for (int i = dataSet.size(); i > 0; i--) {
            add(dataSet.getRecords().get(i - 1));
        }
        return this;
    }

    public void add(Record record) {
        if (items == null) {
            items = new HashMap<>();
        }
        String key = null;
        for (String field : fields) {
            Object val = record.getField(field);
            if (val == null) {
                val = "null";
            }
            key = key == null ? val.toString() : key + ";" + val.toString();
        }
        items.put(key, record);
    }

    public Record get(Object key) {
        if (items == null) {
            items = new HashMap<>();
            add(dataSet);
        }
        if (key == null) {
            return items.get("null");
        } else {
            return items.get(key.toString());
        }
    }

    public Record get(Object[] keys) {
        if (keys == null || keys.length == 0) {
            throw new RuntimeException("值列表不能为空或者长度不能为0");
        }
        if (fields.size() != keys.length) {
            throw new RuntimeException("参数名称 与 值列表长度不匹配");
        }

        String key = null;
        for (Object obj : keys) {
            if (obj == null) {
                obj = "null";
            }
            key = key == null ? obj.toString() : key + ";" + obj.toString();
        }

        return get(key);
    }

    public void clear() {
        items = null;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public void setFields(String keyFields) {
        if (keyFields == null || "".equals(keyFields)) {
            throw new RuntimeException("参数名称不能为空");
        }
        if (!keyFields.equals(this.keyFields)) {
            fields.clear();
            for (String key : keyFields.split(";")) {
                if (dataSet.size() > 0 && dataSet.getFieldDefs().size() > 0 && !dataSet.exists(key)) {
                    throw new RuntimeException(String.format("字段 %s 不存在，无法查找！", key));
                }
                fields.add(key);
            }
            this.keyFields = keyFields;
            clear();
        }
    }

    public boolean existsKey(String field) {
        return fields.contains(field);
    }
}
