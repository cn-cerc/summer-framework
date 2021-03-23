package cn.cerc.ui.core;

/**
 * 行模式输出，排版为：标签 | 输入框 | 按钮/提示文字, 输入框有2种类型：可修改 / 只读
 * 
 * @author ZhangGong
 *
 */
public interface ISimpleLine extends IField {

    void outputLine(HtmlWriter html);

}
