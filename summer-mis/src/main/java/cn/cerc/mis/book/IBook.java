package cn.cerc.mis.book;

public interface IBook extends IBookEnroll {
    // 初始化（仅调用一次）
    void init(IBookManage manage);

    // 初始化（ 会被每月调用）
    void ready();

    // 将帐本的更新，保存到数据库中
    void save();
}
