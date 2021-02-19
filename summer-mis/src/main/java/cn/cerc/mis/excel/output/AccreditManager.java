package cn.cerc.mis.excel.output;

public interface AccreditManager {
    /**
     * 
     * @param handle
     *            环境参数
     * @return 返回是否可以通过本次权限
     */
    public boolean isPass(Object handle);

    /**
     * 
     * @return 返回需要授权的权限描述
     */
    public String getDescribe();
}
