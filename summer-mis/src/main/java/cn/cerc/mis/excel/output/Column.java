package cn.cerc.mis.excel.output;

import cn.cerc.core.Record;

public abstract class Column {
    public static final String LEFT = "left";
    public static final String RIGHT = "right";
    public static final String CENTER = "center";
    // 对应数据集字段名
    private String code;
    // 对应数据集字段标题
    private String name;
    // 列宽度
    private int width;
    // 排列方式
    private String align;
    // 数据源
    private Record record;

    // 标记
    private int tag = 0;

    public Column() {

    }

    public Column(String code, String name, int width) {
        this.code = code;
        this.name = name;
        this.width = width;
    }

    // 取得数据
    public abstract Object getValue();

    public String getString() {
        return record.getString(code);
    }

    public String getCode() {
        return code;
    }

    public Column setCode(String code) {
        this.code = code;
        return this;
    }

    public String getName() {
        return name;
    }

    public Column setName(String name) {
        this.name = name;
        return this;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public int getWidth() {
        return width;
    }

    public Column setWidth(int width) {
        this.width = width;
        return this;
    }

    public String getAlign() {
        return align;
    }

    public Column setAlign(String align) {
        this.align = align;
        return this;
    }

    public int getTag() {
        return tag;
    }

    public Column setTag(int tag) {
        this.tag = tag;
        return this;
    }
}