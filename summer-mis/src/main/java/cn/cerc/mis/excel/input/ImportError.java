package cn.cerc.mis.excel.input;

public interface ImportError {
    /**
     * 自定义错误处理器
     * 
     * @param e
     *            列效验异常
     * @return true表示错误已处理，允许继续导入，否则予以中止
     * @throws Exception
     *             通用异常
     */
    public boolean process(ColumnValidateException e) throws Exception;
}
