package cn.cerc.core;

public class SyncDataSet {
    private DataSet source;
    private DataSet target;
    private String keyFields;

    public SyncDataSet(DataSet source, DataSet target, String keyFields) {
        this.source = source;
        this.target = target;
        this.keyFields = keyFields;
    }

    public int execute(ISyncDataSet sync) throws SyncUpdateException {
        int result = 0;
        source.first();
        while (source.fetch()) {
            result++;
            Record src = source.getCurrent();
            String[] fields = keyFields.split(";");
            Object[] values = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                values[i] = src.getField(fields[i]);
            }
            Record tar = target.locate(keyFields, values) ? target.getCurrent() : null;
            sync.process(src, tar);
        }
        target.first();
        while (target.fetch()) {
            Record tar = target.getCurrent();
            String[] fields = keyFields.split(";");
            Object[] values = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                values[i] = tar.getField(fields[i]);
            }
            if (!source.locate(keyFields, values)) {
                result++;
                sync.process(null, tar);
            }
        }
        return result;
    }
}
