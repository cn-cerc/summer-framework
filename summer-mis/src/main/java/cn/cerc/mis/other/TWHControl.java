package cn.cerc.mis.other;

import cn.cerc.core.DataSet;
import cn.cerc.core.Record;

public enum TWHControl {
    whcNone, whcHead, whcBody;

    /*
     * 取得单头仓别
     */
    public String getHeadWH(Record head) {
        return this == TWHControl.whcNone ? "仓库" : head.getString("WHCode_");
    }

    /*
     * 取得单身仓别
     */
    public String getBodyWH(DataSet body, String defaultWH) {
        if (this == whcBody)
            return body.getCurrent().getString("CWCode_");
        else
            return defaultWH;
    }
}