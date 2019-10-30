package cn.cerc.mis.excel.output;

public class DateTimeColumn extends Column {

    @Override
    public Object getValue() {
        return getRecord().getDateTime(getCode()).getData();
    }
}
