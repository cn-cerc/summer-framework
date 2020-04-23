package cn.cerc.mis.core;

import cn.cerc.core.IHandle;

import java.util.List;

public interface IAppMenu {

    // 返回系统菜单定义
    @Deprecated
    /**
     * 此项在最新版的地藤版本中不再使用
     */
    MenuItem getItem(String menuId);

    // 返回指定父菜单下的所有子菜单
    @Deprecated
    /**
     * 此项在最新版的地藤版本中不再使用
     */
    List<MenuItem> getList(IHandle handle, String parentId, boolean security);

}
