package cn.cerc.core;

import java.util.Comparator;

public class RecordComparator implements Comparator<Record> {
    private String[] fields;

    public RecordComparator(String[] fields) {
        this.fields = fields;
    }

    @Override
    public int compare(Record o1, Record o2) {
        long tmp = 0;
        for (String item : fields) {
            if (item == null || "".equals(item)) {
                throw new RuntimeException("排序字段为空");
            }
            String[] params = item.split(" ");
            String field = params[0];
            Object v1 = o1.getField(field);
            if (v1 instanceof Double || v1 instanceof Long) {
                double df = o1.getDouble(field) - o2.getDouble(field);
                if (df == 0) {
                    tmp = 0;
                } else {
                    tmp = df > 0 ? 1 : -1;
                }
            } else if (v1 instanceof Integer) {
                tmp = o1.getInt(field) - o2.getInt(field);
            } else if (v1 instanceof TDate) {
                tmp = o1.getDate(field).compareTo(o2.getDate(field));
            } else if (v1 instanceof TDateTime) {
                tmp = o1.getDateTime(field).compareTo(o2.getDateTime(field));
            } else {
                tmp = o1.getString(field).compareTo(o2.getString(field));
            }

            if (tmp != 0) {
                if (params.length == 1 || "ASC".equalsIgnoreCase(params[1])) {
                    return tmp > 0 ? 1 : -1;
                } else if ("DESC".equalsIgnoreCase(params[1])) {
                    return tmp > 0 ? -1 : 1;
                } else {
                    throw new RuntimeException(String.format("不支持【%s】排序模式", params[1]));
                }
            }
        }
        return 0;
    }
}
