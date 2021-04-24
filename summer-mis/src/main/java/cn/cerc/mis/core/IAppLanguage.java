package cn.cerc.mis.core;

import cn.cerc.db.core.IHandle;

public interface IAppLanguage extends IHandle {
    
    String getLanguageId(String defaultValue);
}
