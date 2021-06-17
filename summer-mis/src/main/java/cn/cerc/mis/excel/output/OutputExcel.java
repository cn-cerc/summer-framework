package cn.cerc.mis.excel.output;

import jxl.write.WritableSheet;
import jxl.write.WriteException;

public interface OutputExcel {
    void output(FormTemplate formTemplate, WritableSheet sheet) throws WriteException;
}
