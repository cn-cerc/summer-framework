package cn.cerc.ui.core;

import cn.cerc.core.Record;

public interface IColumn extends IField {

    String format(Record record);
}
