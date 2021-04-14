package cn.cerc.ui.plugins;

import cn.cerc.ui.parts.UIComponent;

public interface IContextDefine extends IPlugins {
    
    boolean attach(UIComponent sender, String funcCode);
}
