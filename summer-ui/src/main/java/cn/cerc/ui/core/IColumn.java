package cn.cerc.ui.core;

import cn.cerc.core.Record;

public interface IColumn extends IField {

    String format(Record record);
    
    default void outputColumn(HtmlWriter html, Record record) {
        html.print(format(record));
    }
}
