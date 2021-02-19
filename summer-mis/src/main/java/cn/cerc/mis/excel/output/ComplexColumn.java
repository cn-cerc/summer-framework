package cn.cerc.mis.excel.output;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.core.Record;

public class ComplexColumn extends Column {
    private List<String> fields = new ArrayList<>();

    public ComplexColumn() {
        super();
    }

    public ComplexColumn(String[] code, String name, int width) {
        super();
        StringBuffer strBuff = new StringBuffer();
        for (String field : code) {
            fields.add(field);
            strBuff.append(field);
        }
        this.setCode(strBuff.toString());
        this.setName(name);
        this.setWidth(width);
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    @Override
    public String getValue() {
        Record record = this.getRecord();
        StringBuffer buff = new StringBuffer();
        for (String field : fields) {
            if (record.hasValue(field)) {
                if (buff.length() > 0)
                    buff.append(",");
                buff.append(record.getString(field));
            }
        }
        return buff.toString();
    }
}
