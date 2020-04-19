package cn.cerc.core;

public interface ISyncDataSet {
    void process(Record src, Record tar) throws SyncUpdateException;
}
