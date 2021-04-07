package cn.cerc.mis.core;

import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.SupportHandle;

/**
 * 请改使用 Handle
 * 
 * @author ZhangGong 2021/3/13
 *
 */
@Deprecated
public class AbstractHandle extends Handle implements SupportHandle {

    @Override
    @Deprecated
    public void init(IHandle handle) {
        this.setHandle(handle);
    }

}
