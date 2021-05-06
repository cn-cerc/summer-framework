package cn.cerc.mis.core;

import cn.cerc.db.core.IHandle;

public interface IMemoryCache {
    
    void resetCache(IHandle handle, boolean isFirst);
    
}
