package cn.cerc.mis.excel.output;

public class ProfitRateColumn extends Column {

    @Override
    public Object getValue() {
        return getRecord().getDouble(getCode()) + "%";
    }

}
