package cn.cerc.mis.tools;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.mis.book.BookDataList;
import cn.cerc.mis.book.IBook;
import cn.cerc.mis.book.IBookData;
import cn.cerc.mis.book.IBookManage;
import cn.cerc.mis.book.UpdateBook;
import cn.cerc.mis.book.VirtualData;
import cn.cerc.mis.other.BookOptions;
import cn.cerc.core.IHandle;
import cn.cerc.core.TDateTime;

public class UpdateManager implements IBookManage {
    private static final Logger log = LoggerFactory.getLogger(UpdateManager.class);
    private IHandle handle;
    private String initMonth;
    private List<UpdateBook> books = new ArrayList<>();
    private BookDataList dataList;
    private TimingList timer = new TimingList();
    private DurationSplit duration;
    private DurationSection section;
    private boolean previewUpdate;
    private boolean locked = false;
    private String partCode;

    public UpdateManager(IHandle handle) {
        this.handle = handle;
        initMonth = BookOptions.getAccInitYearMonth(handle);
    }

    @Override
    public void setDateRange(TDateTime beginDate, TDateTime endDate, boolean forceExecute) {
        if (initMonth.compareTo(beginDate.getYearMonth()) > 0)
            beginDate = TDateTime.fromYearMonth(initMonth);

        if (beginDate.compareTo(endDate) > 0)
            throw new RuntimeException(String.format("起始日期(%s)大于截止日期(%s)", beginDate, endDate));

        duration = new DurationSplit(beginDate, endDate);
        dataList = new BookDataList(new DurationSection(beginDate, TDateTime.Now()));
    }

    public void execute() throws DataUpdateException {
        locked = true; // 防止调用错误

        if (handle == null)
            throw new RuntimeException("handle is null");

        if (duration == null)
            throw new RuntimeException("duration is null");

        if (books.size() == 0)
            throw new RuntimeException("帐本对象不允许为空！");

        if (dataList.size() == 0)
            return;

        timer.get("process total").start();
        log.info(String.format("排序 %d 项数据并传给帐本", dataList.size()));
        for (DurationSection section : duration) {
            this.section = section;

            for (IBook book : books)
                book.ready();

            log.info(String.format("book enroll yearMonth: %s", section.getMonthFrom()));
            for (IBookData bookData : dataList) {
                if (bookData.getDate().getYearMonth().equals(section.getMonthFrom())) {
                    dataList.check(bookData);
                    for (UpdateBook book : books) {
                        if (bookData instanceof VirtualData) {
                            VirtualData data = (VirtualData) bookData;
                            if (data.getBook() == book)
                                book.enroll(data.getBookData(), true);
                        } else {
                            boolean ok = book.enroll(bookData, false);
                            if (ok && book.isKnowMonth()) {
                                if (TDateTime.Now().compareMonth(bookData.getDate()) > 0) {
                                    for (int i = 1; i <= TDateTime.Now().compareMonth(bookData.getDate()); i++)
                                        dataList.add(new VirtualData(book, bookData, i));
                                }
                            }
                        }
                    }
                }
            }

            log.info(String.format("更新帐本数据"));
            for (UpdateBook book : books)
                book.update();
            log.info(String.format("保存帐本变动"));
            for (IBook book : books)
                book.save();
            log.info("完成");
        }

        timer.get("process total").stop();
    }

    public IHandle getHandle() {
        return handle;
    }

    public void setHandle(IHandle handle) {
        this.handle = handle;
    }

    public UpdateManager addBook(UpdateBook book) {
        if (locked)
            throw new RuntimeException("locked is true");
        books.add(book);
        book.init(this);
        return this;
    }

    public <T> T add(T data) {
        if (locked)
            throw new RuntimeException("locked is true");
        if (data instanceof IBookData) {
            dataList.addItem((IBookData) data);
            return data;
        } else
            throw new RuntimeException("data not is BookData");
    }

    public String getInitMonth() {
        return initMonth;
    }

    @Override
    public boolean isBatchMode() {
        return false;
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
        return previewUpdate;
    }

    public void setPreviewUpdate(boolean previewUpdate) {
        this.previewUpdate = previewUpdate;
    }

    @Override
    public BookDataList getDatas() {
        return dataList;
    }

    @Override
    public String getPartCode() {
        return partCode;
    }

    public void setPartCode(String partCode) {
        this.partCode = partCode;
    }
}
