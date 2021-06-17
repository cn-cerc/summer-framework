package cn.cerc.db.other;

import cn.cerc.core.DataSet;
import cn.cerc.core.Record;

import java.util.HashMap;
import java.util.Map;

public class SumRecord extends Record {
    private static final long serialVersionUID = -8836802853579764175L;
    private DataSet dataSet;
    private Map<String, Double> fields = new HashMap<>();

    public SumRecord(DataSet dataSet) {
        this.setDataSet(dataSet);
    }

    public SumRecord addField(String field) {
        if (!fields.containsKey(field)) {
            fields.put(field, 0.0);
        }
        return this;
    }

    public SumRecord addField(String... args) {
        for (String field : args) {
            if (!fields.containsKey(field)) {
                fields.put(field, 0.0);
            }
        }
        return this;
    }

    public SumRecord run() {
        for (Record rs : this.dataSet) {
            for (String field : this.fields.keySet()) {
                Double value = fields.get(field);
                value += rs.getDouble(field);
                fields.put(field, value);
            }
        }
        for (String field : this.fields.keySet()) {
            Double value = fields.get(field);
            this.setField(field, value);
        }
        return this;
    }

    @Override
    public DataSet getDataSet() {
        return dataSet;
    }

    @Override
    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    public Map<String, Double> getFields() {
        return fields;
    }

    /**
     * 仅在调试时使用
     */
    public void print() {
        for (String field : fields.keySet()) {
            System.out.println(String.format("%s: %s", field, "" + fields.get(field)));
        }
    }

}
