package cn.cerc.ui.core;

/**
 * 表格输出模式，其输出位于 table > tr > td 中，输入框有2种类型：可修改 / 只读
 * 
 * @author ZhangGong
 *
 */
public interface IColumn extends IField {

    void outputColumn(HtmlWriter html);
}
