package cn.cerc.mis.book;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;

import cn.cerc.mis.tools.DurationSection;
import cn.cerc.core.TDateTime;

public class BookDataList implements Iterable<IBookData>, Iterator<IBookData> {
    private List<IBookData> items = new ArrayList<>();
    private DurationSection section;
    private int itemNo = -1;

    public BookDataList(DurationSection section) {
        this.section = section;
    }

    public void add(IBookData data) {
        check(data);
        items.add(data);
    }

    public void addItem(IBookData data) {
        items.add(data);
    }

    public void check(IBookData data) {
        TDateTime dateFrom = section.getDateFrom();
        TDateTime dateTo = section.getDateTo();
        String s1 = dateFrom.getDate();
        String s2 = dateTo.getDate();
        String s3 = data.getDate().getDate();
        if (s1.compareTo(s3) > 0)
            throw new RuntimeException(String.format("日期错误：对象日期 %s 不能小于起始日期 %s", data.getDate(), dateFrom));
        if (s2.compareTo(s3) < 0)
            throw new RuntimeException(String.format("日期错误：对象日期 %s 不能大于结束日期 %s", data.getDate(), dateTo));
        if (!data.check())
            throw new RuntimeException("对象记录有误，无法作业：" + new Gson().toJson(data));
    }

    public TDateTime getDateFrom() {
        return section.getDateFrom();
    }

    public TDateTime getDateTo() {
        return section.getDateTo();
    }

    public static void main(String[] args) {
        TDateTime dateFrom = TDateTime.Now().incDay(1);
        System.out.println(TDateTime.Now().compareDay(dateFrom));
    }

    @Override
    public Iterator<IBookData> iterator() {
        items.sort(new Comparator<IBookData>() {
            @Override
            public int compare(IBookData o1, IBookData o2) {
                return o2.getDate().compareDay(o1.getDate());
            }
        });
        this.itemNo = -1;
        return this;
    }

    @Override
    public boolean hasNext() {
        itemNo++;
        return items.size() > itemNo;
    }

    @Override
    public IBookData next() {
        return items.get(itemNo);
    }

    public int size() {
        return items.size();
    }
}
