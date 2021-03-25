package cn.cerc.ui.columns;

import cn.cerc.core.Record;

public interface IDataColumn extends IColumn {

    boolean isReadonly();

    Object setReadonly(boolean readonly);

    boolean isHidden();

    Object setHidden(boolean hidden);

    Record getRecord();

    void setRecord(Record record);
}
