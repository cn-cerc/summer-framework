package cn.cerc.mis.book;

public interface IBookEnroll {
    // 将数据登记到帐本中, 若virtual为true则表示为处理分月专用
    public boolean enroll(IBookData bookData, boolean virtual);

}
