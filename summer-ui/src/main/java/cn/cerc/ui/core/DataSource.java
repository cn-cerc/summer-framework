package cn.cerc.ui.core;

import cn.cerc.core.DataSet;

public interface DataSource extends IDataSetOwner{

    void addField(IField field);

    boolean isReadonly();

    void updateValue(String id, String code);
}
