package cn.cerc.core;

import java.util.List;

public class PostFieldException extends RuntimeException {
    private static final long serialVersionUID = -7000564918024722819L;
    // Post之DataQuery
    private DataSet query;
    // 数据表中的原始字段
    private List<String> fields;

    public PostFieldException(DataSet dataQuery, List<String> fields) {
        this.query = dataQuery;
        this.fields = fields;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public DataSet getQuery() {
        return query;
    }

    public void setQuery(DataSet query) {
        this.query = query;
    }

    @Override
    public String getMessage() {
        StringBuffer buff = new StringBuffer();
        buff.append("not find field:");
        for (String field : query.getFieldDefs().getFields()) {
            if (!fields.contains(field)) {
                buff.append(" " + field);
            }
        }
        return buff.toString();
    }
}
