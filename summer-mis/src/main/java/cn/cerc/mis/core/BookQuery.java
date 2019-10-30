package cn.cerc.mis.core;

import cn.cerc.core.IDataOperator;
import cn.cerc.core.IHandle;
import cn.cerc.core.Record;
import cn.cerc.db.mysql.SqlOperator;
import cn.cerc.db.mysql.SqlQuery;

public class BookQuery extends SqlQuery implements IDataOperator {
    private static final long serialVersionUID = 7447239243975915295L;
    private SqlOperator operator;
    private IHandle handle;

    public BookQuery(IHandle handle) {
        super(handle);
        this.handle = handle;
    }

    @Override
    public SqlOperator getDefaultOperator() {
        if (operator == null) {
            SqlOperator def = new SqlOperator(handle);
            String tableName = SqlOperator.findTableName(this.getSqlText().getText());
            def.setTableName(tableName);
            operator = def;
        }
        this.setOperator(operator);
        return operator;
    }

    @Override
    public boolean insert(Record record) {
        String corpNo = record.getString("CorpNo_");
        if (!handle.getCorpNo().equals(corpNo))
            throw new RuntimeException(String.format("corpNo: %s, insert error value: %s", handle.getCorpNo(), corpNo));
        return operator.insert(record);
    }

    @Override
    public boolean update(Record record) {
        String corpNo = record.getString("CorpNo_");
        if (!handle.getCorpNo().equals(corpNo))
            throw new RuntimeException(String.format("corpNo: %s, update error value: %s", handle.getCorpNo(), corpNo));
        return operator.update(record);
    }

    @Override
    public boolean delete(Record record) {
        String corpNo = record.getString("CorpNo_");
        if (!handle.getCorpNo().equals(corpNo))
            throw new RuntimeException(String.format("corpNo: %s, delete error value: %s", handle.getCorpNo(), corpNo));
        return operator.delete(record);
    }
}
