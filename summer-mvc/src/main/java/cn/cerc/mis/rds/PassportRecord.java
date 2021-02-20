package cn.cerc.mis.rds;

public class PassportRecord {
    // 是否超级用户
    private boolean admin;
    // 是否可以执行
    private boolean execute;
    // 是否可以打印输出
    private boolean print;
    // 是否可以导出
    private boolean output;
    // 是否可以增加
    private boolean append;
    // 是否可以修改
    private boolean modify;
    // 是否可以删除
    private boolean delete;
    // 是否可以生效
    private boolean finish;
    // 是否可以撤消或结案
    private boolean cancel;
    // 是否可以作废
    private boolean recycle;

    public PassportRecord() {

    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
        this.execute = admin;
        this.print = admin;
        this.output = admin;
        this.append = admin;
        this.modify = admin;
        this.delete = admin;
        this.finish = admin;
        this.cancel = admin;
        this.recycle = admin;
    }

    public boolean isExecute() {
        return execute;
    }

    public void setExecute(boolean execute) {
        this.execute = execute;
    }

    public boolean isPrint() {
        return print;
    }

    public void setPrint(boolean print) {
        this.print = print;
    }

    public boolean isOutput() {
        return output;
    }

    public void setOutput(boolean output) {
        this.output = output;
    }

    public boolean isAppend() {
        return append;
    }

    public void setAppend(boolean append) {
        this.append = append;
    }

    public boolean isModify() {
        return modify;
    }

    public void setModify(boolean modify) {
        this.modify = modify;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public boolean isRecycle() {
        return recycle;
    }

    public void setRecycle(boolean recycle) {
        this.recycle = recycle;
    }
}
