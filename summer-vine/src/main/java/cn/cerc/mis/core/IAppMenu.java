package cn.cerc.mis.core;

import cn.cerc.db.core.IHandle;

import java.util.List;

public interface IAppMenu {

    // 返回系统菜单定义
    public MenuItem getItem(String menuId);

    // 返回指定父菜单下的所有子菜单
    public List<MenuItem> getList(IHandle handle, String parentId, boolean security);
}
