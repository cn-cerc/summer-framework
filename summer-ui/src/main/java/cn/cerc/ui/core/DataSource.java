package cn.cerc.ui.core;

public interface DataSource extends IDataSetOwner{

    void addField(IField field);

    void updateValue(String id, String code);
}
