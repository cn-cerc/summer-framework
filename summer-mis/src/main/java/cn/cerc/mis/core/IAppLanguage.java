package cn.cerc.mis.core;

import cn.cerc.core.ISession;

public interface IAppLanguage {
    
    String getLanguageId(ISession session, String defaultValue);
}
