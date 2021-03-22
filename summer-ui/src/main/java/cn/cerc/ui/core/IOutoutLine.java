package cn.cerc.ui.core;

public interface IOutoutLine extends IField {
    
    // 只读输出
    void outputReadonly(HtmlWriter html);

    // 普通输出
    void outputDefault(HtmlWriter html);
    
    default void outputLine(HtmlWriter html) {
        if(this.isReadonly())
            outputReadonly(html);
        else
            outputDefault(html);
    }

}
