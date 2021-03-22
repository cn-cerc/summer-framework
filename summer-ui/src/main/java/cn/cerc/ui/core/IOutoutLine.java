package cn.cerc.ui.core;

import cn.cerc.core.Record;

public interface IOutoutLine extends IField {
    
    // 只读输出
    void outputReadonly(HtmlWriter html, Record record);

    // 普通输出
    void outputDefault(HtmlWriter html, Record record);
    
    default void outputLine(HtmlWriter html, Record record) {
        if(this.isReadonly())
            outputReadonly(html, record);
        else
            outputDefault(html, record);
    }

}
