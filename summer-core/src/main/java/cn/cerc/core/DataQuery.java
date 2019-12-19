package cn.cerc.core;

import java.util.ArrayList;

public abstract class DataQuery extends DataSet {
    private static final long serialVersionUID = 7316772894058168187L;
    protected boolean active = false;
    protected IHandle handle;
    // 批次保存模式，默认为post与delete立即保存
    private boolean batchSave = false;
    private SqlText sqlText = new SqlText();

    public DataQuery(IHandle handle) {
        this.handle = handle;
    }

    // 打开数据集
    public abstract DataQuery open();

    // 批量保存
    public abstract void save();

    // 返回保存操作工具
    public abstract IDataOperator getOperator();

    // 是否批量保存
    public boolean isBatchSave() {
        return batchSave;
    }

    public void setBatchSave(boolean batchSave) {
        this.batchSave = batchSave;
    }

    /**
     * 增加sql指令内容，调用此函数需要自行解决sql注入攻击！
     *
     * @param sql 要增加的sql指令内容
     * @return 返回对象本身
     */
    protected DataQuery add(String sql) {
        sqlText.add(sql);
        return this;
    }

    protected DataQuery add(String format, Object... args) {
        ArrayList<Object> items = new ArrayList<>();
        for (Object arg : args) {
            if (arg instanceof String) {
                items.add(Utils.safeString((String) arg));
            } else {
                items.add(arg);
            }
        }
        return this.add(String.format(format, items.toArray()));
    }

    @Deprecated // 请改使用 getSqlText().getText
    public String getCommandText() {
        return this.sqlText.getText();
    }

    protected SqlText getSqlText() {
        return this.sqlText;
    }

    protected void setSqlText(SqlText sqlText) {
        this.sqlText = sqlText;
    }

    @Deprecated // 请改使用 getSqlText().clear
    public DataQuery emptyCommand() {
        this.sqlText.clear();
        return this;
    }

    public boolean getActive() {
        return active;
    }

    public DataQuery setActive(boolean active) {
        this.active = active;
        return this;
    }

}
