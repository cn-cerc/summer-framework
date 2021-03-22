package cn.cerc.ui.core;

import cn.cerc.core.Record;

public interface IField extends IReadonlyOwner {
    String getTitle();

    String getField();

//    int getWidth();
    default int getWidth() {
        return 1;
    }

    String getAlign();

	String getText(Record record);
    
}
