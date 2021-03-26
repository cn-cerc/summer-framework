package cn.cerc.ui.core;

import cn.cerc.core.Record;

public interface IFormatColumn extends IField {

    String format(Object value);

    @Override
    default int getWidth() {
        return 1;
    }
}
