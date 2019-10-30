package cn.cerc.mis.tools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.mis.book.BookDataList;
import cn.cerc.mis.book.IBook;
import cn.cerc.mis.book.IBookData;
import cn.cerc.mis.book.IBookManage;
import cn.cerc.mis.book.IBookSource;
import cn.cerc.mis.book.IResetBook;
import cn.cerc.mis.book.VirtualData;
import cn.cerc.mis.other.BookOptions;
import cn.cerc.core.IHandle;
import cn.cerc.core.TDateTime;

public class ResetManager implements IBookManage {
    private static final Logger log = LoggerFactory.getLogger(ResetManager.class);
    private IHandle handle;
    private String initMonth;
    private List<IBookSource> sources = new ArrayList<>();
    private List<IResetBook> books = new ArrayList<>();
    private TimingList timer = new TimingList();
    private DurationSplit duration;
    private DurationSection section;
    // 预览更新而不真正保存
    private boolean previewUpdate;
    // 指定回算料号
    private String partCode;

    public ResetManager(IHandle handle) {
        this.handle = handle;
        initMonth = BookOptions.getAccInitYearMonth(handle);
    }

    @Override
    public void setDateRange(TDateTime beginDate, TDateTime endDate, boolean forceExecute) {
        if (initMonth.compareTo(beginDate.getYearMonth()) > 0)
            beginDate = TDateTime.fromYearMonth(initMonth);

        if (beginDate.compareTo(endDate) > 0)
            throw new RuntimeException(String.format("起始日期(%s)大于截止日期(%s)", beginDate, endDate));

        // 非强制执行时，增加对执行时间的判断
        if (!forceExecute) {
            Calendar cal = Calendar.getInstance();
            if (cal.get(Calendar.HOUR_OF_DAY) >= 8 && (cal.get(Calendar.HOUR_OF_DAY) < 18)) {
                if (TDateTime.Now().compareMonth(beginDate) > 1)
                    throw new RuntimeException("在工作高峰期间(08:00-18:00)，为保障其它用户可用性，只允许处理最近2个月的数据！");
            }
        }
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
            BookDataList dataList = new BookDataList(section);
            this.process(dataList, section);
            dataList = null;
            System.gc();
        }

        timer.get("process total").stop();
    }

    private void process(BookDataList dataList, DurationSection section) throws Exception {
        this.section = section;
        log.info(String.format("corpNo:%s, init:%s, book total: %d", handle.getCorpNo(), initMonth, books.size()));
        log.info(String.format("dateFrom: %s, dateTo: %s", section.getDateFrom(), section.getDateTo()));
        log.info(String.format("取得数据源, source total:%d", sources.size()));

        Timing pt1 = timer.get("sources load");
        Timing pt2 = timer.get("sources output");
        for (IBookSource dataSource : sources) {
            pt1.start();
            dataSource.open(this);
            pt1.stop();
            pt2.start();
            dataSource.output(dataList);
            System.gc();
            pt2.stop();
        }

        pt1 = timer.get("books init").start();
        for (IBook book : books) {
            book.ready();
        }
        pt1.stop();

        try {
            log.info(String.format("排序 %d 项数据并传给帐本", dataList.size()));
            pt1 = timer.get("books enroll").start();
            for (IBookData bookData : dataList) {
                for (IResetBook book : books) {
                    if (bookData instanceof VirtualData) {
                        VirtualData data = (VirtualData) bookData;
                        if (data.getBook() == book)
                            book.enroll(data.getBookData(), true);
                    } else
                        book.enroll(bookData, false);
                }
            }
            pt1.stop();

            log.info(String.format("保存帐本变动"));
            pt1 = timer.get("books save").start();
            log.info(String.format("更新帐本数据"));
            for (IResetBook book : books)
                book.reset();
            for (IBook book : books)
                book.save();
            pt1.stop();
            log.info("完成");
        } catch (DataUpdateException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
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

    public ResetManager addSource(IBookSource bookSource) {
        sources.add(bookSource);
        return this;
    }

    public ResetManager addBook(IResetBook book) {
        books.add(book);
        book.init(this);
        return this;
    }

    public String getInitMonth() {
        return initMonth;
    }

    @Override
    public boolean isBatchMode() {
        return sources.size() > 0;
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

    // 是否预览变更而不保存
    @Override
    public boolean isPreviewUpdate() {
        return previewUpdate;
    }

    public void setPreviewUpdate(boolean previewUpdate) {
        this.previewUpdate = previewUpdate;
    }

    @Override
    public BookDataList getDatas() {
        return null;
    }

    @Override
    public String getPartCode() {
        return partCode;
    }

    public void setPartCode(String partCode) {
        this.partCode = partCode;
    }
}
