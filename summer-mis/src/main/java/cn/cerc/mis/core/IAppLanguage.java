package cn.cerc.mis.core;

import cn.cerc.db.core.ISessionOwner;

public interface IAppLanguage extends ISessionOwner {
    
    String getLanguageId(String defaultValue);
}
