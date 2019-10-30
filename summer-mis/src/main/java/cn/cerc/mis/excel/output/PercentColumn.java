package cn.cerc.mis.excel.output;

import cn.cerc.core.Utils;

public class PercentColumn extends Column {

    @Override
    public Object getValue() {
        String value = Utils.formatFloat("0.##", getRecord().getDouble(getCode()));
        return String.format("%s%%", value);
    }
}
