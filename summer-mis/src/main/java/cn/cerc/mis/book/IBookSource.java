package cn.cerc.mis.book;

public interface IBookSource {
    // 打开数据源
    void open(IBookManage manage);

    // 读取数据到items
    void output(BookDataList list) throws Exception;
}
