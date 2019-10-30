package cn.cerc.mis.book;

/**
 * 帐本记录，对应帐本表中的每一行记录
 * 
 * @author 张弓
 *
 */
public interface IBookRecord {

    // 接收原始数据
    public void write(IBookData bookData);
}
