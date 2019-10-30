package cn.cerc.mis.book;

import cn.cerc.mis.tools.DataUpdateException;

public interface UpdateBook extends IBook {

    // 在过帐时，需要区分年月
    public boolean isKnowMonth();

    // 对登记到帐本的的数据进行更新
    public void update() throws DataUpdateException;
}
