package cn.cerc.mis.excel.input;

import java.text.DecimalFormat;

import cn.cerc.core.Utils;

public class ImportNumberColumn extends ImportColumn {
    @Override
    public Object getValue() {
        return Utils.strToDoubleDef(new DecimalFormat("0.######").format(getRecord().getDouble(getCode())), 0);
    }

    @Override
    public boolean validate(int row, int col, String value) {
        String result = "";
        if (!"".equals(value)) {
            result = String.valueOf(Math.abs(Double.parseDouble(value)));
        } else {
            result = "0";
        }
        return Utils.isNumeric(Utils.formatFloat("0.######", Double.parseDouble(result)));
    }
}
