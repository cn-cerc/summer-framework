package cn.cerc.mis.book;

public interface IBookSource {
    // 打开数据源
    public void open(IBookManage manage);

    // 读取数据到items
    public void output(BookDataList list) throws Exception;
}
