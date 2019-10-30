package cn.cerc.mis.tools;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.mis.book.BookDataList;
import cn.cerc.mis.book.IBookData;
import cn.cerc.mis.book.IBookEnroll;
import cn.cerc.mis.book.IBookManage;
import cn.cerc.mis.book.IBookSource;
import cn.cerc.mis.other.BookOptions;
import cn.cerc.core.IHandle;
import cn.cerc.core.TDateTime;

public class SearchManager implements IBookManage {
    private static final Logger log = LoggerFactory.getLogger(SearchManager.class);
    private IHandle handle;
    private String initMonth;
    private List<IBookSource> sources = new ArrayList<>();
    private List<IBookEnroll> books = new ArrayList<>();
    private BookDataList dataList;
    private TimingList timer = new TimingList();
    private DurationSplit duration;
    private DurationSection section;

    public SearchManager(IHandle handle) {
        this.handle = handle;
        initMonth = BookOptions.getAccInitYearMonth(handle);
    }

    @Override
    public void setDateRange(TDateTime beginDate, TDateTime endDate, boolean forceExecute) {
        if (initMonth.compareTo(beginDate.getYearMonth()) > 0)
            throw new RuntimeException(String.format("起始日期(%s)小于开账年月(%s)", beginDate.getYearMonth(), initMonth));

        if (beginDate.compareTo(endDate) > 0)
            throw new RuntimeException(String.format("起始日期(%s)大于截止日期(%s)", beginDate, endDate));

        duration = new DurationSplit(beginDate, endDate);
    }

    public void execute() throws Exception {
        if (handle == null)
            throw new RuntimeException("handle is null");

        if (duration == null)
            throw new RuntimeException("duration is null");

        if (books.size() == 0)
            throw new RuntimeException("帐本对象不允许为空！");

        timer.get("process total").start();

        for (DurationSection section : duration) {
            dataList = new BookDataList(section);
            this.process(section);
        }

        timer.get("process total").stop();
    }

    private void process(DurationSection section) throws Exception {
        this.section = section;
        log.info(String.format("corpNo:%s, init:%s, book total: %d", handle.getCorpNo(), initMonth, books.size()));
        log.info(String.format("dateFrom: %s, dateTo: %s", section.getDateFrom(), section.getDateTo()));
        log.info(String.format("取得数据源, source total:%d", sources.size()));

        Timing pt1 = timer.get("sources load");
        Timing pt2 = timer.get("sources output");
        for (IBookSource ds : sources) {
            pt1.start();
            ds.open(this);
            pt1.stop();
            pt2.start();
            ds.output(dataList);
            pt2.stop();
        }

        log.info(String.format("排序 %d 项数据并传给帐本", dataList.size()));
        pt1 = timer.get("books enroll").start();
        for (IBookData item : dataList) {
            for (IBookEnroll book : books) {
                book.enroll(item, false);
            }
        }
        pt1.stop();
        log.info("完成");
    }

    public IHandle getHandle() {
        return handle;
    }

    public void setHandle(IHandle handle) {
        this.handle = handle;
    }

    public List<IBookSource> getSources() {
        return sources;
    }

    public SearchManager addSource(IBookSource bookSource) {
        sources.add(bookSource);
        return this;
    }

    public SearchManager addBook(IBookEnroll book) {
        books.add(book);
        return this;
    }

    public String getInitMonth() {
        return initMonth;
    }

    @Override
    public boolean isBatchMode() {
        return sources.size() > 0;
    }

    public BookDataList getDatas() {
        return dataList;
    }

    public TimingList getTimer() {
        return timer;
    }

    // 取得开始日期
    @Override
    public TDateTime getDateFrom() {
        return section.getDateFrom();
    }

    // 取得结束日期
    @Override
    public TDateTime getDateTo() {
        return section.getDateTo();
    }

    @Override
    public boolean isPreviewUpdate() {
        return false;
    }

    @Override
    public String getPartCode() {
        return null;
    }
}
