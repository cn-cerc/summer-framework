package cn.cerc.ui.core;

import cn.cerc.core.Record;

public interface IField {
    String getTitle();

    String getField();

    int getWidth();

    String getAlign();
    
    String getText(Record record);
}
