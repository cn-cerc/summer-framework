package cn.cerc.mis.excel.input;

import cn.cerc.core.Record;

public interface ImportRecord {
    boolean process(Record rs) throws Exception;
}
