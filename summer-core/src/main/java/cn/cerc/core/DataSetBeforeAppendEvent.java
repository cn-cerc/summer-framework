package cn.cerc.core;

public interface DataSetBeforeAppendEvent {

    Record filter(DataSet dataSet, Record record);

}
