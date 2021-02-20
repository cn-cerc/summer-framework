package cn.cerc.mis.book;

import cn.cerc.core.TDateTime;

public class VirtualData implements IBookData {
    private TDateTime date;
    private IBook book;
    private IBookData bookData;

    public VirtualData(IBook book, IBookData bookData, int month) {
        this.book = book;
        this.bookData = bookData;
        this.date = bookData.getDate().incMonth(month).monthBof();
    }

    public IBookData getBookData() {
        return bookData;
    }

    public IBook getBook() {
        return book;
    }

    @Override
    public TDateTime getDate() {
        return date;
    }

    @Override
    public boolean check() {
        return true;
    }

    public boolean isOwner(IBook book) {
        return this.book == book;
    }
}
