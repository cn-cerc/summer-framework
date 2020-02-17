package cn.cerc.ui.core;

import cn.cerc.core.DataSet;

public interface DataSource {

    void addField(IField field);

    DataSet getDataSet();

    boolean isReadonly();

    void updateValue(String id, String code);
}
