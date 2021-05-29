package cn.cerc.mis.tools;

import cn.cerc.core.TDateTime;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Iterator;

public class DurationSplit implements Iterable<DurationSection>, Iterator<DurationSection> {
    private TDateTime beginDate;
    private TDateTime endDate;
    private TDateTime dateFrom;
    private TDateTime dateTo;
    private int total;

    public DurationSplit(TDateTime beginDate, TDateTime endDate) {
        this.beginDate = beginDate;
        this.endDate = endDate;
        if (beginDate == null) {
            throw new RuntimeException("beginDate is null");
        }
    }

    public static void main(String[] args) throws ParseException {
        DurationSplit duration = new DurationSplit(TDateTime.StrToDate("2016-07-01"), TDateTime.StrToDate("201609"));
        for (DurationSection section : duration) {
            System.out.println(String.format("beginDate: %s, endDate: %s", section.getDateFrom(), section.getDateTo()));
        }
    }

    public TDateTime getDateFrom() {
        return dateFrom;
    }

    public TDateTime getDateTo() {
        return dateTo;
    }

    public TDateTime getBeginDate() {
        return beginDate;
    }

    public TDateTime getEndDate() {
        return endDate;
    }

    @Override
    public Iterator<DurationSection> iterator() {
        dateFrom = beginDate;
        dateTo = beginDate.monthEof();
        total = -1;
        return this;
    }

    @Override
    public boolean hasNext() {
        if (++total == 0) {
            return beginDate.getData().before(endDate.getData());
        }

        dateFrom = dateTo.incMonth(1).monthBof();
        return endDate.getData().after(dateTo.getData());
    }

    @Override
    public DurationSection next() {
        if (total == 0) {
            dateFrom = beginDate;
            dateTo = beginDate.monthEof();
        } else {
            dateFrom = dateTo.incMonth(1).monthBof();
            dateTo = dateFrom.monthEof();
        }
        if (dateTo.compareDay(endDate) > 0) {
            dateTo = endDate;
        }
        if ("00:00:00".equals(dateTo.getTime())) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateTo.incDay(1).getData());
            cal.set(Calendar.MILLISECOND, cal.get(Calendar.MILLISECOND) - 1);
            dateTo.setData(cal.getTime());
        }
        return new DurationSection(dateFrom, dateTo);
    }
}
