package cn.cerc.ui.core;

import cn.cerc.core.Record;

public interface IFormatColumn extends IField {

    String format(Record value);

    @Override
    default int getWidth() {
        return 1;
    }
}
