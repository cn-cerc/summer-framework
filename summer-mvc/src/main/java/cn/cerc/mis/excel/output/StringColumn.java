package cn.cerc.mis.excel.output;

public class StringColumn extends Column {

    public StringColumn() {
        super();
    }

    public StringColumn(String code, String name, int width) {
        super(code, name, width);
    }

    // 取得数据
    @Override
    public Object getValue() {
        return this.getString();
    }

}
