package cn.cerc.ui.core;

/**
 * 行模式输出，排版为：标签 | 输入框 | 按钮/提示文字, 输入框有2种类型：可修改 / 只读
 * 
 * @author ZhangGong
 *
 */
public interface ISimpleLine extends IField {

    // 只读输出
    void outputReadonly(HtmlWriter html);

    // 普通输出
    void outputEditer(HtmlWriter html);

    default void outputLine(HtmlWriter html) {
        if (this.isReadonly())
            outputReadonly(html);
        else
            outputEditer(html);
    }

}
