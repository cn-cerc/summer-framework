package cn.cerc.mis.excel.output;

import cn.cerc.core.Utils;

public class NumberColumn extends Column {

    public NumberColumn() {
        super();
    }

    public NumberColumn(String code, String name, int width) {
        super(code, name, width);
    }

    @Override
    public Object getValue() {
        return Utils.strToDoubleDef(Utils.formatFloat("0.####", getRecord().getDouble(getCode())), 0);
    }
}
