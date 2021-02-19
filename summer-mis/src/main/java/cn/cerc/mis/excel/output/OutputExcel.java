package cn.cerc.mis.excel.output;

import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public interface OutputExcel {
    public void output(FormTemplate formTemplate, WritableSheet sheet) throws RowsExceededException, WriteException;
}
