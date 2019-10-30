package cn.cerc.core;

public interface IDataOperator {

    public boolean insert(Record record);

    public boolean update(Record record);

    public boolean delete(Record record);

}
