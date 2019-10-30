package cn.cerc.mis.tools;

import cn.cerc.core.TDateTime;

public class DurationSection {
    private TDateTime dateFrom;
    private TDateTime dateTo;

    public DurationSection(TDateTime dateFrom, TDateTime dateTo) {
        super();
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    public TDateTime getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(TDateTime dateFrom) {
        this.dateFrom = dateFrom;
    }

    public TDateTime getDateTo() {
        return dateTo;
    }

    public void setDateTo(TDateTime dateTo) {
        this.dateTo = dateTo;
    }

    public String getMonthFrom() {
        return dateFrom.getYearMonth();
    }
}
