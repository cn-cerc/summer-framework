package cn.cerc.mis.excel.input;

public class ImportStringColumn extends ImportColumn {

    // 取得数据
    @Override
    public Object getValue() {
        return this.getString();
    }

    @Override
    public boolean validate(int row, int col, String value) {
        return true;
    }

}
