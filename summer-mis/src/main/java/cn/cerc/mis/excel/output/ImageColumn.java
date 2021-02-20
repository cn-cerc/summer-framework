package cn.cerc.mis.excel.output;

public class ImageColumn extends Column {

    public ImageColumn() {
        super();
    }

    public ImageColumn(String code, String name, int width) {
        super(code, name, width);
    }

    @Override
    public Object getValue() {
        return this.getString();
    }
}
