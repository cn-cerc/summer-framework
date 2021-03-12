package cn.cerc.mis.excel.output;

import cn.cerc.core.Utils;

// TODO: 2021/3/11 与NumberColumn一样，仅用于区分 ExcelTemplate 导出时设置不同格式
public class NumberFormatColumn extends Column {

    public NumberFormatColumn() {
        super();
    }

    public NumberFormatColumn(String code, String name, int width) {
        super(code, name, width);
    }

    @Override
    public Object getValue() {
        return Utils.strToDoubleDef(Utils.formatFloat("0.####", getRecord().getDouble(getCode())), 0);
    }
}
