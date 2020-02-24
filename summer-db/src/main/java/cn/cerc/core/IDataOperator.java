package cn.cerc.core;

public interface IDataOperator {

    boolean insert(Record record);

    boolean update(Record record);

    boolean delete(Record record);

}
