package cn.cerc.db.mysql;

import cn.cerc.core.ISqlConnection;

public abstract class SqlConnection implements ISqlConnection {
    private int tag;

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

}
